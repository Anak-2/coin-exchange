package coin.stock.presentation;

import coin.stock.application.MarketService;
import coin.stock.domain.MarketProperties;
import coin.stock.global.base_entity.CreatedAt;
import coin.stock.global.response.CollectionApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MarketController{

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @PatchMapping("/market")
    public void updateMarket(){
        marketService.fetchAllMarketsWithRestTemplate();
    }

    @GetMapping("/market")
    public CollectionApiResponse<MarketProperties> getMarkets(){
        return CollectionApiResponse.from(marketService.getAllMarketProperties());
    }
}
