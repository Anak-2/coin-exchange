package coin.stock.presentation;

import coin.stock.application.TickerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TickerController {

    private final TickerService tickerService;

    public TickerController(TickerService tickerService) {
        this.tickerService = tickerService;
    }


}
