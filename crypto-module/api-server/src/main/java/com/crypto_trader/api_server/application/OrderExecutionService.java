package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.entities.Order;
import com.crypto_trader.api_server.domain.events.TickerProcessingEvent;
import com.crypto_trader.api_server.infra.OrderRepository;
import com.crypto_trader.api_server.infra.SimpleMarketRepository;
import com.crypto_trader.api_server.infra.TickerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderExecutionService {

    private final TickerRepository tickerRepository;
    private final SimpleMarketRepository marketRepository;
    private final OrderRepository orderRepository;

    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;


    private final Map<String, Sinks.Many<Ticker>> sinkMap = new HashMap<>();
    private final Map<String, Disposable> subscriptionMap = new HashMap<>();

    @Autowired
    public OrderExecutionService(TickerRepository tickerRepository,
                                 SimpleMarketRepository marketRepository,
                                 OrderRepository orderRepository,
                                 ApplicationEventPublisher publisher,
                                 ObjectMapper objectMapper) {
        this.tickerRepository = tickerRepository;
        this.marketRepository = marketRepository;
        this.orderRepository = orderRepository;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        // 시장 코드 업데이트에 따른 구독 설정
        marketRepository.marketCodesUpdates().subscribe(this::updateMarketSubscriptions);

        // Ticker 채널 구독 설정
        tickerRepository.getChannel().subscribe(value -> handleTickerMessage(value.getMessage()));
    }

    /**
     * 시장 코드 업데이트에 따라 구독과 Sink를 재설정
     */
    private void updateMarketSubscriptions(List<String> codes) {
        clearSubscriptionsAndSinks();

        for (String code : codes) {
            Sinks.Many<Ticker> sink = Sinks.many().unicast().onBackpressureBuffer();
            sinkMap.put(code, sink);

            Disposable subscription = sink.asFlux()
                    .sampleFirst(Duration.ofMillis(500))  // 0.5 초에 한 번 처리(쓰로틀링)
                    .subscribe(ticker -> publisher.publishEvent(new TickerProcessingEvent(this, ticker)));

            subscriptionMap.put(code, subscription);
        }
    }

    /**
     * 기존의 구독과 Sink를 정리
     */
    private void clearSubscriptionsAndSinks() {
        subscriptionMap.values().forEach(Disposable::dispose);
        subscriptionMap.clear();
        sinkMap.clear();
    }

    /**
     * Redis에서 받은 Ticker 메시지를 처리하여 적절한 Sink에 발행
     */
    private void handleTickerMessage(String message) {
        try {
            Ticker ticker = objectMapper.readValue(message, Ticker.class);
            tickerRepository.save(ticker);

            Sinks.Many<Ticker> sink = sinkMap.get(ticker.getMarket());
            if (sink != null) {
                sink.tryEmitNext(ticker).orThrow();
            } else {
                throw new RuntimeException("Sink not found for market: " + ticker.getMarket());
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process Ticker message", e);
        }
    }

    /**
     * TickerProcessingEvent 를 처리하여 주문 실행
     */
    @EventListener
    public void processTicker(TickerProcessingEvent event) {
        Ticker ticker = event.getTicker();
        processOrderExecution(ticker.getMarket(), ticker.getTradePrice());
    }

    @Transactional
    public void processOrderExecution(String market, double tradePrice) {
        int pageSize = 100000;
        int pageNumber = 0;

        Page<Order> ordersChunk = null;
        do {
            PageRequest pageable = PageRequest.of(pageNumber, pageSize);
            ordersChunk = orderRepository.findByMarketWithPrice(market, tradePrice, pageable);

            // 기본 parallelStream: ForkJoinPool.commonPool() 사용
            // - 시스템의 가용 CPU 코어 수에 따라 설정

            // 스레드 수를 직접 조정하려면 별도의 ForkJoinPool 사용
            ordersChunk.getContent().parallelStream().forEach(Order::execution);

            pageNumber++;
        } while (!ordersChunk.isLast());
    }
}
