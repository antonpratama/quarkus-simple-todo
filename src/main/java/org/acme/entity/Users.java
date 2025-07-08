package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Users extends PanacheEntity {

  @Column(unique = true)
  public String username;

  public String password;

  @OneToMany(mappedBy = "users")
  public List<Todo> todos;
}
