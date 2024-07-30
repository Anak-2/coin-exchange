package coin.stock.application;

import coin.stock.domain.TickerProperties;
import coin.stock.entity.Market;
import coin.stock.entity.Ticker;
import coin.stock.repository.MarketRepository;
import coin.stock.repository.TickerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static coin.stock.global.config.AppConfig.SocketTicketUUID;
import static coin.stock.global.config.AppConfig.externalApiSocketSession;

@Slf4j
@Service
public class TickerService {

    private final RestTemplate restTemplate;
    private final TickerRepository tickerRepository;
    private final ObjectMapper objectMapper;

    public TickerService(RestTemplate restTemplate, TickerRepository tickerRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.tickerRepository = tickerRepository;
        this.objectMapper = objectMapper;
    }

    public void save(TickerProperties tickerProperties){
        Ticker entity = tickerProperties.toEntity();
        tickerRepository.save(entity);
    }

    public void fetchTickerPerMarketWithRestTemplate(List<String> market) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.upbit.com/v1/ticker?");
        market.forEach(m -> {
            sb.append("markets=").append(m).append("&");
        });
        String url = sb.toString();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            List<TickerProperties> tickerProperties = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<TickerProperties>>(){}
            );
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tickerProperties);
            System.out.println(prettyJson);
        } catch (Exception e) {
            log.error("Error When FetchTicker: {}", e.getMessage());
        }
    }

    public void fetchTickerWithWebsocket(List<String> market){
        if (externalApiSocketSession != null && externalApiSocketSession.isOpen()) {
            String payload = createPayload(market);
            log.info("Payload: {}", payload);
            try {
                externalApiSocketSession.sendMessage(new TextMessage(payload));
            } catch (Exception e) {
                log.error("Error When FetchTicker: {}", e.getMessage());
            }
        } else {
            log.info("socket session is null or closed");
        }
    }

    private String createPayload(List<String> marketCodes) {
        List<Object> request = List.of(
                Map.of("ticket", SocketTicketUUID),
                Map.of("type", "ticker", "codes", marketCodes),
                Map.of("format", "DEFAULT")
        );

        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("Error creating payload", e);
        }
    }
}
