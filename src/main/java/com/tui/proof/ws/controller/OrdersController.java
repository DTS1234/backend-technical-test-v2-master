package com.tui.proof.ws.controller;

import com.tui.proof.OrderService;
import com.tui.proof.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RestController
@RequiredArgsConstructor
public class OrdersController {

  private final OrderService orderService;

  @PostMapping("/orders")
  Order createOrder(@RequestBody Order order) {
    log.info(String.format("Order creation - %s", order.toString()));



    return Order.builder().build();
  }
}