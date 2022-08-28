package com.skyTech.springapirest.Models.Entities;

import com.sun.istack.NotNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "clientes") //Buena practica (En minuscula, plural y si es compuesta separadas por _)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 4, max = 12, message = "debe contener entre 4 y 12 letras")
    @NotEmpty(message = "no puede estar vacio")
    private String nombre;

    @Column(nullable = false)
    @NotEmpty(message = "no puede estar vacio")
    @Size(min = 4, max = 12, message = "debe contener entre 4 y 12 letras")
    private String apellido;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "no puede estar vacio")
    @Email(message = "debe contener un formato email valido")
    private String email;

    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt;


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------//

    @PrePersist
    public void prePersist(){
        createAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
