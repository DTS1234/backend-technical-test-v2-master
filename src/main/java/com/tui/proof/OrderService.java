package com.tui.proof;

import com.tui.proof.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class OrderService {

    public Order createOrder(@Valid Order orderRequest) {
        return null;
    }

}
