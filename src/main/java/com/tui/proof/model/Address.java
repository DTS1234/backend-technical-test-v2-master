package com.tui.proof.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class Address {
  @NotEmpty
  private String street;
  @NotEmpty
  @Size(max = 5, min = 5)
  private String postcode;
  @NotEmpty
  private String city;
  @NotEmpty
  private String country;
}
