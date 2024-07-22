package coin.stock.entity;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
public class Market {

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
