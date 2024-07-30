package coin.stock.entity;

import coin.stock.global.annotation.Association;
import coin.stock.global.base_entity.BaseTimeEntity;
import coin.stock.global.base_entity.CreatedAt;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Ticker extends CreatedAt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Association(description = "Market Entity 매핑 id")
    private String market;
    private double tradePrice;
    private double accTradePrice24h;

    protected Ticker() {}

    @Builder
    public Ticker(final String market, final double tradePrice, final double accTradePrice24h) {
        this.market = market;
        this.tradePrice = tradePrice;
        this.accTradePrice24h = accTradePrice24h;
    }
}
