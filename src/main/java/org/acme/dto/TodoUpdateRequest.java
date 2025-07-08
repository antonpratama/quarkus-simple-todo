package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class TodoUpdateRequest {

  @NotBlank(message = "Title tidak boleh kosong")
  @Size(min = 3, max = 100, message = "Title harus antara 3 sampai 100 karakter")
  @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Title hanya boleh huruf, angka, dan spasi")
  public String title;

  public boolean completed;

  @Past(message = "Tanggal deadline tidak boleh di masa lampau")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  public LocalDateTime dueDate;
}
