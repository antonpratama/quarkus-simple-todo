package org.acme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest {

  @NotBlank(message = "Username tidak boleh kosong")
  @Size(min = 3, max = 20, message = "Username harus antara 3 sampai 20 karakter")
  public String username;

  @NotBlank(message = "Password tidak boleh kosong")
  public String password;
}
