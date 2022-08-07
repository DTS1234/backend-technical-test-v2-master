package com.tui.proof.web.controller;

import com.tui.proof.orders.OrderService;
import com.tui.proof.orders.model.Order;
import com.tui.proof.web.model.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RestController
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping(value = "/orders", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info(String.format("Order creation - %s", request));
        Order orderCreated = orderService.createOrder(request.getOrder(), request.getClient());
        log.info(String.format("Order created - %s", orderCreated.toString()));
        return orderCreated;
    }



}

