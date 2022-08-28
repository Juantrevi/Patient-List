package com.skyTech.springapirest.Models.Services;

import com.skyTech.springapirest.Models.Entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteService {

    public List<Cliente> findAll();

    public Page<Cliente> findAllPageable(Pageable pageable);

    public Cliente save(Cliente cliente);

    public void delete(Long id);

    public Cliente findById(Long id);




}
