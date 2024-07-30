package coin.stock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebSocketTest {

    static WebSocketSession webSocketSession;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException, IOException {
        // WebSocket 클라이언트 설정
        StandardWebSocketClient client = new StandardWebSocketClient();

        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                client,
                new SimpleWebSocketHandler(),
                URI.create("wss://api.upbit.com/websocket/v1")
        );

        manager.start();
        while(webSocketSession == null){
            Thread.sleep(3 * 1000);
        }
        while(webSocketSession.isOpen()){
            System.out.println("호출");
            String payload = "[{\"ticket\":\"1251231241254125123412412\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]}]";
            webSocketSession.sendMessage(new TextMessage(payload));
            Thread.sleep(100 * 1000);
        }
    }

    private static class SimpleWebSocketHandler extends BinaryWebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            System.out.println("after connection established");
            webSocketSession = session;
        }

        @Override
        public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
            ByteBuffer payload = message.getPayload();
            String jsonString = StandardCharsets.UTF_8.decode(payload).toString();
            System.out.println("Received Binary Message: " + jsonString);

            try {
                JsonNode jsonNode = objectMapper.readTree(jsonString);
                System.out.println("Parsed JSON: " + jsonNode.toPrettyString());
            } catch (Exception e) {
                System.err.println("Error parsing binary message: " + e.getMessage());
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.err.println("Error: " + exception.getMessage());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            System.out.println("Closed: " + status);
        }
    }

    private static String getRequest() throws JsonProcessingException {
        List<Map<String, Object>> request = new ArrayList<>();

        Map<String, Object> ticketField = new HashMap<>();
        ticketField.put("ticket", UUID.randomUUID().toString());
        request.add(ticketField);

        Map<String, Object> typeField1 = new HashMap<>();
        typeField1.put("type", "ticker");
        typeField1.put("codes", Arrays.asList("KRW-BTC", "KRW-ETH"));
        request.add(typeField1);

        Map<String, Object> formatField = new HashMap<>();
        formatField.put("format", "DEFAULT");
        request.add(formatField);

        String requestJson = objectMapper.writeValueAsString(request);
        System.out.println(requestJson);
        return requestJson;
    }
}
