package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TodoRequest {

  @NotBlank(message = "Title tidak boleh kosong")
  @Size(min = 3, max = 100, message = "Title harus antara 3 sampai 100 karakter")
  @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Title hanya boleh huruf, angka, dan spasi")
  public String title;

  public boolean completed;

  @Future(message = "Tanggal deadline harus di masa depan")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  public LocalDateTime dueDate;
}
