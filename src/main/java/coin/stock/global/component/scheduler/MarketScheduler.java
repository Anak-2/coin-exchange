package coin.stock.global.component.scheduler;

import coin.stock.application.MarketService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class MarketScheduler {

    private final MarketService marketService;

    public MarketScheduler(MarketService marketService) {
        this.marketService = marketService;
    }

    @PostConstruct
    public void init() {
        marketService.fetchAllMarketsWithRestTemplate();
    }
}
