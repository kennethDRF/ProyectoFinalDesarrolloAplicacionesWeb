package com.ufide.barbex.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "barbero_servicios")
public class BarberoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbero_id", nullable = false)
    private Usuario barbero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    public BarberoServicio() {
    }

    public BarberoServicio(Usuario barbero, Servicio servicio) {
        this.barbero = barbero;
        this.servicio = servicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getBarbero() {
        return barbero;
    }

    public void setBarbero(Usuario barbero) {
        this.barbero = barbero;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}
