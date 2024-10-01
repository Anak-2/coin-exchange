package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.OrderExecutionService;
import com.crypto_trader.api_server.application.OrderService;
import com.crypto_trader.api_server.application.dto.OrderCancelRequestDto;
import com.crypto_trader.api_server.application.dto.OrderCreateRequestDto;
import com.crypto_trader.api_server.application.dto.OrderResponseDto;
import com.crypto_trader.api_server.auth.PrincipalUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderExecutionService orderExecutionService;

    public OrderController(OrderService orderService, OrderExecutionService orderExecutionService) {
        this.orderService = orderService;
        this.orderExecutionService = orderExecutionService;
    }

    @PostMapping("/create")
    public OrderResponseDto createOrder(@AuthenticationPrincipal PrincipalUser principalUser,
                                        @RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        assert principalUser.getUser() != null;
        return orderService.createOrder(principalUser, orderCreateRequestDto);
    }

    @PatchMapping("cancel")
    public OrderResponseDto cancelOrder(@AuthenticationPrincipal PrincipalUser principalUser,
                                        @RequestBody OrderCancelRequestDto orderCancelRequestDto) {
        assert principalUser.getUser() != null;
        return orderService.cancelOrder(principalUser, orderCancelRequestDto);
    }

    // test
    @GetMapping
    public List<OrderResponseDto> getOrders(@AuthenticationPrincipal PrincipalUser principalUser) {
        assert principalUser.getUser() != null;
        return orderService.getAllOrders(principalUser);
    }

    @GetMapping("/test")
    public String getOrdersT(@AuthenticationPrincipal PrincipalUser principalUser) {
        orderExecutionService.oldProcessOrderExecution("KRW-BTC", 99999999);
        return "success";
    }
}
