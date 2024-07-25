package coin.stock.implement.market;

import coin.stock.entity.Market;
import coin.stock.global.annotation.Implement;
import coin.stock.repository.MarketRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Implement
public class MarketUpdater {

    private final MarketRepository marketRepository;

    public MarketUpdater(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    @Transactional
    public void updateMarketsByMap(List<Market> newMarkets){
        List<Market> existingMarkets = marketRepository.findAll();
        Map<String, Market> existingMarketMap = existingMarkets.stream()
                .collect(Collectors.toMap(Market::getMarket, market -> market));
        List<Market> marketsToUpdate = newMarkets.stream()
                .filter(newMarket -> {
                    Market existingMarket = existingMarketMap.get(newMarket.getMarket());
                    if (existingMarket != null) {
                        boolean isChanged = !existingMarket.getKoreanName().equals(newMarket.getKoreanName()) ||
                                !existingMarket.getEnglishName().equals(newMarket.getEnglishName());
                        if (isChanged) {
                            existingMarket.setKoreanName(newMarket.getKoreanName());
                            existingMarket.setEnglishName(newMarket.getEnglishName());
                        }
                        return isChanged;
                    }
                    return true; // 새로운 데이터
                })
                .toList();

        marketRepository.saveAll(marketsToUpdate);
    }
}
