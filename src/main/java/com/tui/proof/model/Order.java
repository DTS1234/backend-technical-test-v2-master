package com.tui.proof.model;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @NotEmpty
    private String number;
    @NotNull
    private Address deliveryAddress;
    @Min(5)
    @Max(15)
    private int pilotes;
    private BigDecimal orderTotal;
}
