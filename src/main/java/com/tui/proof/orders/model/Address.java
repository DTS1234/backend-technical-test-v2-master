package com.tui.proof.orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
  @NotBlank(message = "Street value is required!")
  private String street;
  @NotEmpty
  @Size(min = 5, max = 5, message = "Postcode should consist of 5 numbers.")
  @Pattern(regexp = "^[0-9]*$", message = "Postcode should consist of 5 numbers.")
  private String postcode;
  @NotBlank(message = "City value is required!")
  @Pattern(regexp = "^[a-zA-Z]+$", message = "City value is invalid!")
  private String city;
  @NotBlank(message = "Country value is required!")
  @Pattern(regexp = "^[a-zA-Z]+$", message = "Country value is invalid!")
  private String country;
}
