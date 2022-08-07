package com.tui.proof;

import com.tui.proof.model.Address;
import com.tui.proof.model.Order;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService subject = new OrderService();

    @Test
    void shouldThrowIfAddressDataIsInvalid() {
        Order orderRequest = Order.builder()
                .deliveryAddress(null)
                .build();

        Assertions.assertThatThrownBy(() -> subject.createOrder(orderRequest))
                .isInstanceOf(MethodArgumentNotValidException.class);
    }
}
