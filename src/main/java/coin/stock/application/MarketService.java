package coin.stock.application;

import coin.stock.domain.MarketProperties;
import coin.stock.repository.MarketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MarketService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final MarketRepository marketRepository;

    public MarketService(RestTemplate restTemplate, WebClient.Builder webClientBuilder, MarketRepository marketRepository) {
        this.restTemplate = restTemplate;
        this.webClient = webClientBuilder.baseUrl("https://api.upbit.com/v1").build();
        this.marketRepository = marketRepository;
    }

    public String fetchAllMarketsWithRestTemplate() {
        String url = "https://api.upbit.com/v1/market/all";
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            List<MarketProperties> marketProperties = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<MarketProperties>>(){}
            );
            marketRepository.saveAll(marketProperties.stream().map(MarketProperties::toEntity).toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Mono<String> getAllMarketsWithWebClient() {
        return this.webClient.get()
                .uri("/market/all")
                .retrieve()
                .bodyToMono(String.class);
    }
}
