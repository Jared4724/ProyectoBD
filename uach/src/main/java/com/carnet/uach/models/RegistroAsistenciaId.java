package com.carnet.uach.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class RegistroAsistenciaId implements Serializable {

    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "matricula")
    private Long matricula; // Suponiendo que la matricula es Long (como id_usuario)
}
