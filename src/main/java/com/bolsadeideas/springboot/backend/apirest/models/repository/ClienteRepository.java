package com.bolsadeideas.springboot.backend.apirest.models.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

	
}
