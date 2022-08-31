package com.skyTech.springapirest.Controllers;

import com.skyTech.springapirest.Models.Entities.Cliente;
import com.skyTech.springapirest.Models.Services.IClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ClienteRestController {

    @Autowired
    private IClienteService clienteService;

    private final Logger log = LoggerFactory.getLogger(ClienteRestController.class);


    @GetMapping("/clientes")
    public List<Cliente> index(){

        return clienteService.findAll();
    }

    @GetMapping("/clientes/page/{page}")
    public Page<Cliente> indexPageable(@PathVariable int page){

        return clienteService.findAllPageable(PageRequest.of(page, 4));
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> findCliente(@PathVariable Long id){

        Cliente cliente = null;
        Map<String, Object> response = new HashMap<>();

        try {
            cliente = clienteService.findById(id);
        } catch (DataAccessException e){
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (cliente == null){
            response.put("message", "El cliente con el ID: " + id.toString() + " no existe en la base de datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result){

        Cliente cliente1 = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()){

            //FORMA ANTERIOR AL JDK 8

//            List<String> errors = new ArrayList<>();
//            for(FieldError err: result.getFieldErrors()){
//                errors.add("El campo" + err.getField() + " " + err.getDefaultMessage());
//            }

            //UTILIZANDO STREAMS

            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {

            cliente1 = clienteService.save(cliente);

        } catch (DataAccessException e){
            response.put("message", "Error al realizar insert en la base de datos");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "El cliente ha sido creado con exito");
        response.put("cliente", cliente1);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, @PathVariable Long id, BindingResult result){

        Map<String, Object> response = new HashMap<>();

        Cliente clienteActual = clienteService.findById(id);
        Cliente clienteUpdate = null;

        if (result.hasErrors()){

            //FORMA ANTERIOR AL JDK 8

//            List<String> errors = new ArrayList<>();
//            for(FieldError err: result.getFieldErrors()){
//                errors.add("El campo" + err.getField() + " " + err.getDefaultMessage());
//            }

            //UTILIZANDO STREAMS

            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo" + " " + err.getField() + " " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("error", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if (clienteActual == null) {
            response.put("message", "El cliente con el ID: " + id.toString() + " no existe en la base de datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            clienteActual.setApellido(cliente.getApellido());
            clienteActual.setNombre(cliente.getNombre());
            clienteActual.setEmail(cliente.getEmail());

            clienteUpdate = clienteService.save(clienteActual);

        }catch (DataAccessException e){
                response.put("message", "Error al actualizar en la base de datos");
                response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        response.put("message", "El cliente ha sido actualizado con exito");
        response.put("cliente", clienteUpdate);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

    }

    @DeleteMapping("clientes/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){

        Map<String, Object> response = new HashMap<>();

        try {
            Cliente cliente = clienteService.findById(id);
            String nombreFotoAnterior = cliente.getFoto();

            if(nombreFotoAnterior != null && nombreFotoAnterior.length() > 0){
                Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
                File archivoFotoAnterior = rutaFotoAnterior.toFile();
                if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()){
                    archivoFotoAnterior.delete();
                }
            }
            clienteService.delete(id);

        }catch (DataAccessException e){
            response.put("message", "Error al eliminar en la base de datos");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "El cliente fue eliminado");

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @PostMapping("/clientes/upload")
    public ResponseEntity<?> upload(@RequestParam(name = "archivo")MultipartFile archivo, @RequestParam(name = "id") Long id){

        Map<String, Object> response = new HashMap<>();

        Cliente cliente = clienteService.findById(id);

        if(!archivo.isEmpty()){
            String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");

            Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            log.info(rutaArchivo.toString());

            try {
                Files.copy(archivo.getInputStream(), rutaArchivo);
            } catch (IOException e) {
                response.put("message", "Error al subir imagen");
                response.put("error", e.getMessage() + ": " + e.getCause().getMessage());
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String nombreFotoAnterior = cliente.getFoto();

            if(nombreFotoAnterior != null && nombreFotoAnterior.length() > 0){
                Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
                File archivoFotoAnterior = rutaFotoAnterior.toFile();
                if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()){
                    archivoFotoAnterior.delete();
                }
            }

            cliente.setFoto(nombreArchivo);

            clienteService.save(cliente);

            response.put("cliente", cliente);
            response.put("message", "Se subio correctamente la imagen");
        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/uploads/img/{nombreFoto:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){

        Path rutaArchivo = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
        log.info(rutaArchivo.toString());

        Resource recurso = null;

        try {
            recurso = new UrlResource(rutaArchivo.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (!recurso.exists() && !recurso.isReadable()){
            rutaArchivo = Paths.get("src/main/resources/static/images").resolve("no-usuario.png").toAbsolutePath();

            try {
                recurso = new UrlResource(rutaArchivo.toUri());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            log.error("Error, no se pudo cargar la imagen: " + nombreFoto);
        }
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

        return new ResponseEntity<Resource>(recurso,cabecera, HttpStatus.OK);
    }



}
