package com.bolsadeideas.springboot.backend.apirest.controller;

import org.springframework.validation.BindingResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "api")
@CrossOrigin(origins = "*")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return (List<Cliente>) clienteService.findAll();
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 4);
		return clienteService.findAll(pageable);
	}

	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {

		Map<String, Object> resultado = new HashMap<>();
		Cliente cliente = null;
		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			// TODO: handle exception
			resultado.put("mensaje", "Error al realizar en la consulta en la base de datos");
			resultado.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (cliente == null) {
			resultado.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(resultado, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}

	@PostMapping("/clientes")
	public ResponseEntity<?> guardaCliente(@Valid @RequestBody Cliente cliente,BindingResult result) {

		Cliente clientenew = null;
		Map<String, Object> response = new HashMap<>();
		//BAD REQUEST ES CUANDO FALLA UNA VALIDACION WEB HTTP
				if (result.hasErrors()) {

					List<String> errors =result.getFieldErrors()
							.stream()
							.map(err ->"El campo '" + err.getField() +"' "+ err.getDefaultMessage())
							.collect(Collectors.toList());

					response.put("errors", errors);
				    return new ResponseEntity<Map<String, Object>>(response,HttpStatus.BAD_REQUEST);
			
				}
		try {
			clientenew = clienteService.save(cliente);

		} catch (DataAccessException e) {
			// TODO: handle exception
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Cliente ha sido creado con éxito!");
		response.put("cliente", clientenew);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente,BindingResult result, @PathVariable Long id) {
       
		
		Cliente clienteUpdated= null;
		
		Map<String, Object>resultado = new HashMap<>();
		
		//BAD REQUEST ES CUANDO FALLA UNA VALIDACION WEB HTTP
		if (result.hasErrors()) {

			List<String> errors =result.getFieldErrors()
					.stream()
					.map(err ->"El campo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());

			resultado.put("errors", errors);
		    return new ResponseEntity<Map<String, Object>>(resultado,HttpStatus.BAD_REQUEST);
	
		}
		
		Cliente clienteActual = clienteService.findById(id);

		if (clienteActual==null) {
			resultado.put("mensaje", "Error: no se puede editar , el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
		    return new ResponseEntity<Map<String, Object>>(resultado,HttpStatus.NOT_FOUND);
		}
        try {
        	
        	clienteActual.setNombre(cliente.getNombre());
		    clienteActual.setApellido(cliente.getApellido());
		    clienteActual.setEmail(cliente.getEmail());
		    
			clienteUpdated = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			// TODO: handle exception
			resultado.put("mensaje", "Error al actualizar el cliente en la base de datos");
			resultado.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(resultado,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		    
            resultado.put("mensaje", "El cliente ha sido actualizado con éxito!");
            resultado.put("cliente", clienteUpdated );
			return new ResponseEntity<Map<String, Object>>(resultado,HttpStatus.CREATED);
	            		
	}

	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> resultado = new HashMap<>();

		try {
			clienteService.deleteById(id);
		} catch (DataAccessException e) {
			// TODO: handle exception

			resultado.put("mensaje", "Error al eliminar el cliente en la base de datos");
			resultado.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(resultado, HttpStatus.INTERNAL_SERVER_ERROR);
	
		}

		resultado.put("mensaje", "El cliente ha sido eliminado con éxito");
		return new ResponseEntity<Map<String, Object>>(resultado, HttpStatus.OK);

	}

}
