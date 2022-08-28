package com.skyTech.springapirest.Models.Services;

import com.skyTech.springapirest.Models.Dao.IClienteDao;
import com.skyTech.springapirest.Models.Entities.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;


@Service
public class ClienteServiceImpl implements IClienteService{

    @Autowired
    private IClienteDao clienteDao;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAllPageable(Pageable pageable) {
        return clienteDao.findAll(pageable);
    }

    @Override
    @Transactional
    public Cliente save(Cliente cliente) {
        cliente.setEmail(cliente.getEmail().toLowerCase(Locale.ROOT));
        return clienteDao.save(cliente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        clienteDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findById(Long id) {
        //Si no lo encuentra retorna un null
        return clienteDao.findById(id).orElse(null);
    }
}
