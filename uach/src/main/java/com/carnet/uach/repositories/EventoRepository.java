package com.carnet.uach.repositories;

import com.carnet.uach.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    @Query("SELECT e FROM Evento e WHERE e.fechaFin IS NULL OR e.fechaFin > :fecha")
    List<Evento> findDisponibles(@Param("fecha") java.time.LocalDateTime fecha);
}
