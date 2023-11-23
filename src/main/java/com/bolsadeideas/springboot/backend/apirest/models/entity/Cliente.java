package com.bolsadeideas.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name="clientes")
public class Cliente implements Serializable{

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;
	@NotEmpty(message = "no puede estar vacio")
	@Size(min = 4,max = 12 ,message = "el tamaño tiene que estar entre 4 y 12")
	@Column(nullable = true)
	private String nombre;
	@NotEmpty(message = "no puede estar vacio")
	private String apellido;
	@NotEmpty(message = "no puede estar vacia")
	@Email(message = "no es una direccion de correo bien formada")
	@Column(nullable = false,unique = false)
	private String email;
	@NotNull(message = "no puede estar vacio")
	@Temporal(TemporalType.DATE)
	@Column(name = "createat")
	private Date createAt;
	private String foto;


	
}
