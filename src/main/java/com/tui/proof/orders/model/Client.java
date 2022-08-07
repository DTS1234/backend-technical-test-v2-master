package com.tui.proof.orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    private final String regexForTelephone = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{3,6}$";

    @NotBlank(message = "First name is required!")
    private String firstName;
    @NotBlank(message = "Last name is required!")
    private String lastName;
    @Size(min = 9, max = 12, message = "Phone number length must be at least 9 numbers, max 12 with country code.")
    @Pattern(regexp = regexForTelephone, message = "Phone number is invalid!")
    @NotNull(message = "Telephone number is required!")
    private String telephone;
    @Email(message = "Email is invalid!")
    private String email;
}
