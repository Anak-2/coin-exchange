package coin.stock.presentation;

import coin.stock.application.MarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UpbitController {

    private final MarketService marketService;

    public UpbitController(MarketService marketService) {
        this.marketService = marketService;
    }

    @PatchMapping("/market")
    public String updateMarket(){
        return marketService.fetchAllMarketsWithRestTemplate();
    }

    @GetMapping("/market")
    public String getMarket(){
        return marketService.fetchAllMarketsWithRestTemplate();
    }
}
