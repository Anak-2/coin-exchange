package coin.stock.domain;

import coin.stock.entity.Market;

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
}
