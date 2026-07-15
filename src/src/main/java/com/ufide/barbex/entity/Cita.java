package com.ufide.barbex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @NotNull(message = "El monto total es obligatorio")
    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_adelanto", precision = 10, scale = 2)
    private BigDecimal montoAdelanto;

    @Column(name = "adelanto_pagado", nullable = false)
    private boolean adelantoPagado = false;

    @Column(name = "comprobante_adelanto", length = 100)
    private String comprobanteAdelanto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barberia_id", nullable = false)
    private Barberia barberia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbero_id", nullable = false)
    private Usuario barbero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CitaServicio> citaServicios = new ArrayList<>();

    public Cita() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoAdelanto() {
        return montoAdelanto;
    }

    public void setMontoAdelanto(BigDecimal montoAdelanto) {
        this.montoAdelanto = montoAdelanto;
    }

    public boolean isAdelantoPagado() {
        return adelantoPagado;
    }

    public void setAdelantoPagado(boolean adelantoPagado) {
        this.adelantoPagado = adelantoPagado;
    }

    public String getComprobanteAdelanto() {
        return comprobanteAdelanto;
    }

    public void setComprobanteAdelanto(String comprobanteAdelanto) {
        this.comprobanteAdelanto = comprobanteAdelanto;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Barberia getBarberia() {
        return barberia;
    }

    public void setBarberia(Barberia barberia) {
        this.barberia = barberia;
    }

    public Usuario getBarbero() {
        return barbero;
    }

    public void setBarbero(Usuario barbero) {
        this.barbero = barbero;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public List<CitaServicio> getCitaServicios() {
        return citaServicios;
    }

    public void setCitaServicios(List<CitaServicio> citaServicios) {
        this.citaServicios = citaServicios;
    }

    public boolean isVencida() {
        LocalDateTime fechaHoraCita = LocalDateTime.of(this.fecha, this.horaInicio);
        return fechaHoraCita.isBefore(LocalDateTime.now())
                && this.estado != EstadoCita.COMPLETADA
                && this.estado != EstadoCita.CANCELADA
                && this.estado != EstadoCita.RECHAZADA;
    }

    public boolean requiereAdelanto() {
        return montoAdelanto != null && montoAdelanto.compareTo(BigDecimal.ZERO) > 0;
    }
}
