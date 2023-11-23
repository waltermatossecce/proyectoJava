package com.bolsadeideas.springboot.backend.apirest.models.entity;


import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name="login")
public class Login {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String usuario;
  private String password;
}

