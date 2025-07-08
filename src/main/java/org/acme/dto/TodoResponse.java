package org.acme.dto;

import java.time.LocalDateTime;

public class TodoResponse {

  public Long id;

  public String title;

  public boolean completed;

  public LocalDateTime dueDate;

  public UserResponse user;
}
