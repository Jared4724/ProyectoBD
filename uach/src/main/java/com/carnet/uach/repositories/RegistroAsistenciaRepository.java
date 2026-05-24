package com.carnet.uach.repositories;

import com.carnet.uach.models.RegistroAsistencia;
import com.carnet.uach.models.RegistroAsistenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, RegistroAsistenciaId> {
    
    List<RegistroAsistencia> findByAsistenciaConfirmadaFalse();
    
    List<RegistroAsistencia> findByEstudiante_IdUsuarioAndAsistenciaConfirmadaTrue(Long matricula);
    
    List<RegistroAsistencia> findByEstudiante_IdUsuario(Long matricula);
}
