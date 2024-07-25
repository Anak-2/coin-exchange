package coin.stock.application;

import coin.stock.domain.TickerProperties;
import coin.stock.repository.MarketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TickerService {

    private final RestTemplate restTemplate;
    private final MarketRepository marketRepository;
    private final ObjectMapper objectMapper;

    public TickerService(RestTemplate restTemplate, MarketRepository marketRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.marketRepository = marketRepository;
        this.objectMapper = objectMapper;
    }

    public void fetchTickerPerMarketWithRestTemplate(List<String> market) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.upbit.com/v1/ticker?");
        market.forEach(m -> {
            sb.append("markets=").append(m).append("&");
        });
        String url = sb.toString();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            List<TickerProperties> tickerProperties = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<TickerProperties>>(){}
            );
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tickerProperties);
            System.out.println(prettyJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
