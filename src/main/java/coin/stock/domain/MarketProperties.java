package coin.stock.domain;

import coin.stock.entity.Market;

import java.util.List;

public record MarketProperties(
        String market,
        String koreanName,
        String englishName
) {
    public Market toEntity(){
        return Market.builder()
                .market(market)
                .koreanName(koreanName)
                .englishName(englishName)
                .build();
    }

    public static MarketProperties from(Market market){
        return new MarketProperties(market.getMarket(), market.getKoreanName(), market.getEnglishName());
    }

    public static List<Market> toEntities(List<MarketProperties> marketProperties){
        return marketProperties.stream().map(MarketProperties::toEntity).toList();
    }
}
