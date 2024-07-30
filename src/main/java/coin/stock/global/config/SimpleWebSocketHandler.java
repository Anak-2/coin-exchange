package coin.stock.global.config;

import coin.stock.application.TickerService;
import coin.stock.domain.TickerProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SimpleWebSocketHandler extends BinaryWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final TickerService tickerService;

    public SimpleWebSocketHandler(ObjectMapper objectMapper, TickerService tickerService) {
        this.objectMapper = objectMapper;
        this.tickerService = tickerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("after connection established");
        AppConfig.externalApiSocketSession = session;
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer payload = message.getPayload();
        String jsonString = StandardCharsets.UTF_8.decode(payload).toString();
        try {
            TickerProperties tickerProperties = objectMapper.readValue(jsonString, TickerProperties.class);
            tickerService.save(tickerProperties);
            log.info("Parsed JSON: {}", tickerProperties);
        } catch (Exception e) {
            log.error("Error parsing binary message: {}", e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Closed: {}", status);
    }
}
