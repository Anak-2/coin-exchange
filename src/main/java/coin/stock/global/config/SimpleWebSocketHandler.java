package coin.stock.global.config;

import coin.stock.application.BatchService;
import coin.stock.application.TickerService;
import coin.stock.domain.TickerProperties;
import coin.stock.repository.TickerRepository;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SimpleWebSocketHandler extends BinaryWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final TickerRepository tickerRepository;
//    private final BatchService batchService;
    private final List<TickerProperties> buffer = new ArrayList<>();
    private static final int BUFFER_SIZE = 100;

    public SimpleWebSocketHandler(ObjectMapper objectMapper, TickerRepository tickerRepository) {
        this.objectMapper = objectMapper;
        this.tickerRepository = tickerRepository;
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
//            todo: 현재는 마켓 하나하나 insert 문이 나가도록 되어있음. Batch로 쿼리문 줄이기
            buffer.add(tickerProperties);
            if (buffer.size() >= BUFFER_SIZE) {
                log.info("batch save");
//                batchService.saveTickers(new ArrayList<>(buffer));
                tickerRepository.saveAll(buffer.stream().map(TickerProperties::toEntity).toList());
                buffer.clear();
            }
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
