package com.carnet.uach.repositories;

import com.carnet.uach.models.RegistroAsistencia;
import com.carnet.uach.models.RegistroAsistenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, RegistroAsistenciaId> {
}
