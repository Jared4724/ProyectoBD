package com.carnet.uach.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Facultad")
@Data
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facultad")
    private Long idFacultad;

    @Column(name = "nombre_facultad", nullable = false, length = 150)
    private String nombreFacultad;
}
