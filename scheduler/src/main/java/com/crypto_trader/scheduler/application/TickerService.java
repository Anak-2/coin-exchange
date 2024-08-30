package com.crypto_trader.scheduler.application;

import com.crypto_trader.scheduler.infra.TickerWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.List;

import static com.crypto_trader.scheduler.config.WebSocketConst.WEBSOCKET_URL;

@Service
public class TickerService {

    private final TickerWebSocketHandler tickerWebSocketHandler;


    @Autowired
    public TickerService(TickerWebSocketHandler tickerWebSocketHandler) {
        this.tickerWebSocketHandler = tickerWebSocketHandler;

        WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                tickerWebSocketHandler,
                WEBSOCKET_URL
        );
        webSocketConnectionManager.start();
    }

    public void fetchAllTickers(List<String> marketCodes) {
        tickerWebSocketHandler.fetchAllTicker(marketCodes);
    }
}
