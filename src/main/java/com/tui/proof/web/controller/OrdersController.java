package com.tui.proof.web.controller;

import com.tui.proof.orders.OrderService;
import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.web.model.CustomerOrderSearchFilter;
import com.tui.proof.web.model.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;

@Log4j2
@RestController
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping(value = "/orders", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Order createOrder(@Valid @RequestBody OrderRequest request) {
        log.info(String.format("Order creation - %s", request));
        Order orderCreated = orderService.createOrder(request.getOrder(), request.getClient());
        log.info(String.format("Order created - %s", orderCreated.toString()));
        return orderCreated;
    }

    @PutMapping(value = "/orders/{number}")
    public Order updateOrder(@Valid @RequestBody OrderRequest request, @PathVariable String number) {
        log.info(String.format("Order update - %s", request));
        String requestNumber = request.getOrder().getNumber();
        if(requestNumber == null || requestNumber.trim().equals("")) {
            request.getOrder().setNumber(requestNumber);
        }
        Order orderUpdated = orderService.updateOrder(request.getOrder(), request.getClient());
        log.info(String.format("Order updated - %s", orderUpdated));
        return orderUpdated;
    }

    @GetMapping(value = "/orders")
    public Collection<Client> searchForOrdersByClientData(CustomerOrderSearchFilter searchFilter) {
        return orderService.filterOrders(searchFilter);
    }


}

