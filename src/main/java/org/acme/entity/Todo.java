package org.acme.entity;

import jakarta.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Todo extends PanacheEntity {

  public String title;

  public boolean completed;

  public LocalDateTime dueDate;

  @ManyToOne
  @JoinColumn(name = "user_id")
  public Users users;
}
