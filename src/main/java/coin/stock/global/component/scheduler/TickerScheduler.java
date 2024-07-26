package coin.stock.global.component.scheduler;

import coin.stock.application.MarketService;
import coin.stock.application.TickerService;
import coin.stock.entity.Market;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TickerScheduler {

    private final TickerService tickerService;
    private final MarketService marketService;
    private int cnt = 1;

    public TickerScheduler(TickerService tickerService, MarketService marketService) {
        this.tickerService = tickerService;
        this.marketService = marketService;
    }

    @Scheduled(fixedRate = 60000)
    public void scheduledFetchTicker() {
        List<Market> allMarket = marketService.getAllMarket();
        if(!allMarket.isEmpty()){
            List<String> allMarketName = allMarket.stream().map(Market::getMarket).toList();
            List<List<String>> chunkedMarketName = chunkList(allMarketName, 10);
            chunkedMarketName.forEach(tickerService::fetchTickerPerMarketWithRestTemplate);
            cnt++;
        }
    }

    private List<List<String>> chunkList(List<String> list, int chunkSize) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return chunks;
    }
}
