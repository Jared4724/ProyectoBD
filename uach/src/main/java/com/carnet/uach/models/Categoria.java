package com.carnet.uach.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Categoria")
@Data
public class Categoria {

    @Id
    @Column(name = "id_categoria", length = 10)
    private String idCategoria;

    @Column(name = "nombre_categoria", nullable = false, length = 100)
    private String nombreCategoria;

    @Column(length = 255)
    private String descripcion;
}
