package coin.stock.entity;

import coin.stock.global.annotation.Association;
import coin.stock.global.base_entity.BaseTimeEntity;
import coin.stock.global.base_entity.CreatedAt;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Ticker extends CreatedAt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Association(description = "Market Entity 매핑 id")
    private Long marketId;
    private double tradePrice;
    private double accTradePrice24h;
}
