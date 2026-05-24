package com.carnet.uach.repositories;

import com.carnet.uach.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByFechaAfter(java.time.LocalDateTime fecha);
}
