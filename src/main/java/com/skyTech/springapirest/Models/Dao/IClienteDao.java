package com.skyTech.springapirest.Models.Dao;

import com.skyTech.springapirest.Models.Entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClienteDao extends JpaRepository<Cliente, Long> {



}
