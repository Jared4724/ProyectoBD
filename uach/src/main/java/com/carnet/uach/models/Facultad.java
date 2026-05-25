package com.carnet.uach.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Facultad")
@Data
public class Facultad {

    @Id
    @Column(name = "id_facultad", length = 10)
    private String idFacultad;

    @Column(name = "nombre_facultad", nullable = false, length = 150)
    private String nombreFacultad;
}
