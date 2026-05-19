package com.carnet.uach.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Estudiante")
@PrimaryKeyJoinColumn(name = "matricula")
@Data
@EqualsAndHashCode(callSuper = true)
public class Estudiante extends Usuario {

    @Column(nullable = false, length = 100)
    private String carrera;

    @Column(nullable = false)
    private Integer semestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facultad", nullable = false)
    private Facultad facultad;
}
