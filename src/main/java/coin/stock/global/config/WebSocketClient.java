package coin.stock.global.config;

import coin.stock.application.BatchService;
import coin.stock.application.TickerService;
import coin.stock.entity.Market;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebSocketClient {

    private final ObjectMapper objectMapper;
    private final BatchService batchService;

    public WebSocketClient(ObjectMapper objectMapper, BatchService batchService) {
        this.batchService = batchService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void startWebSocketConnection() throws InterruptedException {
        StandardWebSocketClient client = new StandardWebSocketClient();

        WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
                client,
                new SimpleWebSocketHandler(objectMapper, batchService),
                URI.create("wss://api.upbit.com/websocket/v1")
        );
        webSocketConnectionManager.start();
    }
}
