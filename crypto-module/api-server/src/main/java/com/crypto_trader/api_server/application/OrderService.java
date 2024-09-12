package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.application.dto.OrderCancelRequestDto;
import com.crypto_trader.api_server.auth.PrincipalUser;
import com.crypto_trader.api_server.domain.OrderSide;
import com.crypto_trader.api_server.domain.entities.CryptoAsset;
import com.crypto_trader.api_server.domain.entities.Order;
import com.crypto_trader.api_server.domain.entities.OrderState;
import com.crypto_trader.api_server.domain.entities.UserEntity;
import com.crypto_trader.api_server.application.dto.OrderCreateRequestDto;
import com.crypto_trader.api_server.application.dto.OrderResponseDto;
import com.crypto_trader.api_server.infra.OrderRepository;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(PrincipalUser principalUser, OrderCreateRequestDto orderCreateRequestDto) {
        UserEntity user = principalUser.getUser();

        Order order = orderCreateRequestDto.toEntity();
        order.validationWith(user);

        user.getAccount().lock(order.totalPrice());
        orderRepository.save(order);

        return OrderResponseDto.toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders(PrincipalUser principalUser) {
        UserEntity user = principalUser.getUser();

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(OrderResponseDto::toDto)
                .toList();
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public OrderResponseDto cancelOrder(PrincipalUser principalUser, OrderCancelRequestDto dto) {
        UserEntity user = principalUser.getUser();

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!Objects.equals(order.getUser().getId(), user.getId()))
            throw new IllegalStateException("User id does not match");

        if (order.getState() != OrderState.CREATED)
            throw new IllegalStateException("Order can't be cancelled");

        order.cancel(dto.getMarket());

        // remove ??
        // orderRepository.delete(order);

        return new OrderResponseDto();
    }

    @Transactional
    public List<Order> getOrderToProcess(String market, double tradePrice) {
        return orderRepository.findByMarket(market).stream()
                .filter(order -> {
                    double price = order.getPrice().doubleValue();
                    return order.getState() == OrderState.CREATED &&
                            ((order.getSide() == OrderSide.BID) ? tradePrice <= price : tradePrice >= price);
                })
                .toList();
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void processOrderWithLock(Order order) {
        Order currentOrder = orderRepository.findById(order.getId()).orElseThrow(() -> new RuntimeException("Order not found"));

        // 상태가 변경되었는지 재검사
        if (currentOrder.getState() == OrderState.CREATED) {
            currentOrder.execution();
        } else {
            throw new RuntimeException("Order already processed or invalid state");
        }
    }
}
