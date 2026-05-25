package com.carnet.uach.services.impl;

import com.carnet.uach.models.Categoria;
import com.carnet.uach.models.Empleado;
import com.carnet.uach.models.Evento;
import com.carnet.uach.repositories.CategoriaRepository;
import com.carnet.uach.repositories.EventoRepository;
import com.carnet.uach.services.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
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
    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }

    @Override
    public List<Evento> listarEventosDisponibles() {
        return eventoRepository.findByFechaAfter(java.time.LocalDateTime.now());
    }

    @Override
    public List<Evento> filtrarEventosDisponibles(Integer mes, String idCategoria) {
        return eventoRepository.findByFechaAfter(java.time.LocalDateTime.now()).stream()
                .filter(e -> mes == null || e.getFecha().getMonthValue() == mes)
                .filter(e -> idCategoria == null || idCategoria.isEmpty() || 
                             (e.getCategoria() != null && e.getCategoria().getIdCategoria().equals(idCategoria)))
                .collect(Collectors.toList());
    }

}
