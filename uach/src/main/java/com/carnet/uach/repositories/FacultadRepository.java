package com.carnet.uach.repositories;

import com.carnet.uach.models.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultadRepository extends JpaRepository<Facultad, String> {
}
