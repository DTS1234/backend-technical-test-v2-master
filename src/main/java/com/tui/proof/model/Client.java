package com.tui.proof.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class Client {
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  @Size()
  private String telephone;
}
