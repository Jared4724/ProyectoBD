package com.carnet.uach.services.impl;

import com.carnet.uach.models.Categoria;
import com.carnet.uach.models.Empleado;
import com.carnet.uach.models.Evento;
import com.carnet.uach.repositories.CategoriaRepository;
import com.carnet.uach.repositories.EventoRepository;
import com.carnet.uach.services.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    @Override
    public Evento obtenerPorId(Long id) {
        return eventoRepository.findById(id).orElse(null);
    }

    @Override
    // REDIS: Cuando se crea o modifica un evento, esta anotación le dice a Redis
    // que borre (Evict) toda la caché almacenada bajo la clave "eventosDisponibles".
    // Así evitamos mostrar datos desactualizados a los estudiantes.
    @CacheEvict(value = "eventosDisponibles", allEntries = true)
    public void guardarEvento(Evento evento, Long idEmpleadoOrganizador) {
        // Busca la Categoría en su repositorio para asignarla al evento
        if (evento.getCategoria() != null && evento.getCategoria().getIdCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(evento.getCategoria().getIdCategoria()).orElse(null);
            evento.setCategoria(categoria);
        }

        // Asigna el idEmpleadoOrganizador al evento antes de guardarlo
        if (idEmpleadoOrganizador != null) {
            Empleado organizador = new Empleado();
            organizador.setIdUsuario(idEmpleadoOrganizador);
            evento.setOrganizador(organizador);
        }

        eventoRepository.save(evento);
    }

    @Override
    // REDIS: La primera vez que alguien consulte los eventos, Spring irá a Oracle,
    // pero guardará el resultado en Redis bajo la clave "eventosDisponibles".
    // Las siguientes veces, devolverá los eventos desde Redis sin tocar la base de datos Oracle.
    @Cacheable(value = "eventosDisponibles")
    public List<Evento> listarEventosDisponibles() {
        return eventoRepository.findDisponibles(java.time.LocalDateTime.now().minusMonths(1));
    }

}
