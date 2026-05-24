package com.carnet.uach.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "registroasistencia")
@Data
public class RegistroAsistencia {

    @EmbeddedId
    private RegistroAsistenciaId id;

    @ManyToOne
    @MapsId("idEvento")
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @ManyToOne
    @MapsId("matricula")
    @JoinColumn(name = "matricula")
    private Estudiante estudiante;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "asistencia_confirmada", nullable = false)
    private boolean asistenciaConfirmada = false;

    @Column(length = 500, nullable = false)
    private String evidencia;

    @Column(length = 1000)
    private String descripcion;
}
