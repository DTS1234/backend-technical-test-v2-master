package com.tui.proof.web.model;

import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @Valid
    private Order order;
    @Valid
    private Client client;
}
