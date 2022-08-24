package com.tui.proof.orders.model;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String number;

    @Valid
    @NotNull(message = "Delivery address is required!")
    private Address deliveryAddress;
    @Min(value = 5, message = "Number of pilotes have to be equal 5, 10 or 15")
    @Max(value = 15, message = "Number of pilotes have to be equal 5, 10 or 15")
    private int pilotes;
    @Min(value = 0, message = "Order total cannot be a negative value.")
    private BigDecimal orderTotal;
}
