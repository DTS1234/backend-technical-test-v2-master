package com.tui.proof.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
  @NotEmpty
  private String street;
  @NotEmpty
  @Size(max = 5, min = 5)
  @Pattern(regexp = "^[0-9]*$")
  private String postcode;
  @NotEmpty
  private String city;
  @NotEmpty
  private String country;
}
