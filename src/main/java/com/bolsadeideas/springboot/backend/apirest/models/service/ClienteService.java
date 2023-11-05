package com.bolsadeideas.springboot.backend.apirest.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;

public interface ClienteService {

	public List<Cliente>findAll();
	 public Page<Cliente>findAll(Pageable pageable);
	public Cliente save(Cliente cliente);
	public Cliente findById(Long id);
	public void deleteById(Long id);
}
