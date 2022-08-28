package com.skyTech.springapirest.Controllers;

import com.skyTech.springapirest.Models.Entities.Cliente;
import com.skyTech.springapirest.Models.Services.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ClienteRestController {

    @Autowired
    private IClienteService clienteService;


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
            clienteService.delete(id);

        }catch (DataAccessException e){
            response.put("message", "Error al eliminar en la base de datos");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "El cliente fue eliminado");

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }



}
