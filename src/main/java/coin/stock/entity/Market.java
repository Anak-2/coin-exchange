package coin.stock.entity;

import coin.stock.global.base_entity.CreatedAt;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
public class Market extends CreatedAt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String market;
    private String koreanName;
    private String englishName;

    protected Market(){}

    @Builder
    public Market(final String market, final String koreanName, final String englishName) {
        this.market = market;
        this.koreanName = koreanName;
        this.englishName = englishName;
    }
}
