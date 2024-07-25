package coin.stock.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

public record TickerProperties(
        @JsonProperty("market")
        String market,
        @JsonProperty("trade_price")
        double tradePrice,
        @JsonProperty("acc_trade_price_24h")
        double accTradePrice24h
) {
}
