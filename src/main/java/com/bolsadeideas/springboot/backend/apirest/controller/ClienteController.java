package com.bolsadeideas.springboot.backend.apirest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "api")
@CrossOrigin(origins = "*")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	private final Logger log = LoggerFactory.getLogger(ClienteController.class);

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
		    clienteActual.setCreateAt(cliente.getCreateAt());

		    
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
		Cliente cliente = clienteService.findById(id);
		try {

			String nombreFotoAnterior= cliente.getFoto();

			if (nombreFotoAnterior !=null && nombreFotoAnterior.length() > 0){
				Path rutaFotoAnterior =Paths.get("upload").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior =rutaFotoAnterior.toFile();
				if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()){
					archivoFotoAnterior.delete();
				}
			}
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
	//Es un tipo multipartFile 
		//RequesParam va aser un parametro
		@PostMapping("/clientes/upload")
		public ResponseEntity<?>upload(@RequestParam("archivo") MultipartFile archivo ,@RequestParam ("id")Long id){
			
			Map<String, Object>response = new HashMap<>();
			
			Cliente cliente = clienteService.findById(id);
			
			//si es distinto !
			if (!archivo.isEmpty()) {
				String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");			

				Path rutaArchivo = Paths.get("upload").resolve(nombreArchivo).toAbsolutePath();
				log.info(rutaArchivo.toString());
				try {
					Files.copy(archivo.getInputStream(),rutaArchivo );

				} catch (IOException e) {
					// TODO Auto-generated catch block
					response.put("mensaje", "Error al subir la imagen del cliente " + nombreArchivo);
					response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
					return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				}

				String nombreFotoAnterior= cliente.getFoto();

				if (nombreFotoAnterior !=null && nombreFotoAnterior.length() >0){
                  Path rutaFotoAnterior = Paths.get("upload").resolve(nombreFotoAnterior).toAbsolutePath();
				  File archivoFotoAnterior =rutaFotoAnterior.toFile();
				   if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()){
					   archivoFotoAnterior.delete();
				   }
				}

				cliente.setFoto(nombreArchivo);
				clienteService.save(cliente);
				
				
				response.put("cliente", cliente);
				response.put("mensaje", "Has subido correctamente la imagen: " + nombreArchivo);
				
			}
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
		}
		//Añadiendo metodo handler ver imagen desde el controlador
		@GetMapping("/uploads/img/{nombreFoto}")
		public ResponseEntity<Resource>verFoto(@PathVariable String nombreFoto){

			Path rutaArchivo = Paths.get("upload").resolve(nombreFoto).toAbsolutePath();
			log.info(rutaArchivo.toString());
			Resource recurso =null;

			try {
				recurso = new UrlResource(rutaArchivo.toUri());
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			if (!recurso.exists() && !recurso.isReadable()){
				throw  new RuntimeException("Error no se pudo cargar la imagen :" + nombreFoto);
			}

			//Para descargar la imagen
			HttpHeaders cabecera = new HttpHeaders();
			cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() +"\"");
			return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
		}

	//Resource tipo
}
