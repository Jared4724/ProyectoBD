package com.carnet.uach.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Eventos")
@Data
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "nombre_evento", nullable = false, length = 200)
    private String nombreEvento;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(nullable = false, length = 255)
    private String ubicacion;

    @Column(nullable = false)
    private Integer puntos;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false) // Organizador
    private Empleado organizador;

    @Transient
    public LocalDateTime getFechaLimite() {
        return fechaFin != null ? fechaFin.plusMonths(1) : null;
    }
}
