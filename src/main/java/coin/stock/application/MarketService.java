package coin.stock.application;

import coin.stock.domain.MarketProperties;
import coin.stock.entity.Market;
import coin.stock.implement.market.MarketUpdater;
import coin.stock.repository.MarketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MarketService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketRepository marketRepository;
    private final MarketUpdater marketUpdater;

    public MarketService(RestTemplate restTemplate, ObjectMapper objectMapper, MarketRepository marketRepository, MarketUpdater marketUpdater) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.marketUpdater = marketUpdater;
        this.marketRepository = marketRepository;
    }

    public void fetchAllMarketsWithRestTemplate() {
        String url = "https://api.upbit.com/v1/market/all";
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            List<MarketProperties> marketProperties = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<MarketProperties>>(){}
            );
            marketUpdater.updateMarketsByMap(MarketProperties.toEntities(marketProperties));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<MarketProperties> getAllMarketProperties(){
        return marketRepository.findAll().stream().map(MarketProperties::from).toList();
    }

    @Transactional(readOnly = true)
    public List<Market> getAllMarket(){
        return marketRepository.findAll();
    }
}
