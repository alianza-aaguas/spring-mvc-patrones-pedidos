package com.empresa.pedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * PATRON: Rich Domain Model
 * Rol: entidad con comportamiento propio (tieneEmail, tieneMovil) en lugar de
 * exponer getters para que otro decida. Evita el modelo anemico.
 */
@Entity
public class Cliente {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String email;

    private String movil;

    private boolean vip;

    protected Cliente() { }

    public Cliente(Long id, String nombre, String email, String movil, boolean vip) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.movil = movil;
        this.vip = vip;
    }

    public boolean tieneEmail() {
        return email != null && !email.isBlank();
    }

    public boolean tieneMovil() {
        return movil != null && !movil.isBlank();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getMovil() { return movil; }
    public boolean esVip() { return vip; }
}
