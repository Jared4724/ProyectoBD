package com.carnet.uach.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Empleado")
@PrimaryKeyJoinColumn(name = "id_empleado")
@Data
@EqualsAndHashCode(callSuper = true)
public class Empleado extends Usuario {

    @Column(nullable = false, length = 100)
    private String puesto;
}
