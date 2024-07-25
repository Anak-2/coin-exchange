package coin.stock.global.component.scheduler;

import coin.stock.application.MarketService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class PerformanceTester {

    private final MarketService marketService;
    private final MeterRegistry meterRegistry;

    public PerformanceTester(MarketService marketService, MeterRegistry meterRegistry) {
        this.marketService = marketService;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedRate = 60000)
    public void testPerformance() {
        Timer restTemplateTimer = meterRegistry.timer("resttemplate.timer");
        restTemplateTimer.record(marketService::fetchAllMarketsWithRestTemplate);
    }
}