package com.tui.proof.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class Order {
  private String number;
  @NotNull
  private Address deliveryAddress;
  private int pilotes;
  private BigDecimal orderTotal;
}
