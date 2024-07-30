package coin.stock.domain;


import coin.stock.entity.Ticker;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TickerProperties(
        @JsonProperty("code")
        String market,
        @JsonProperty("trade_price")
        double tradePrice,
        @JsonProperty("acc_trade_price_24h")
        double accTradePrice24h
) {

        public Ticker toEntity(){
                return Ticker.builder()
                        .market(market)
                        .tradePrice(tradePrice)
                        .accTradePrice24h(accTradePrice24h)
                        .build();
        }
}
