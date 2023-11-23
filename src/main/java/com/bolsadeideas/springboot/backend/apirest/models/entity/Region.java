package com.bolsadeideas.springboot.backend.apirest.models.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Data
@Table(name="regiones")
@Entity
public class Region implements Serializable {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String nombre;

}
