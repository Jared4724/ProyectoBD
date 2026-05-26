package com.carnet.uach.services;

import com.carnet.uach.models.Evento;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de Eventos.
 */
public interface EventoService {

    /**
     * Obtiene la lista de todos los eventos.
     * 
     * @return Lista de eventos.
     */
    List<Evento> listarTodos();

    /**
     * Obtiene un evento por su ID.
     * 
     * @param id Identificador del evento.
     * @return Evento encontrado o null.
     */
    Evento obtenerPorId(Long id);

    /**
     * Guarda un evento en la base de datos.
     * Busca la categoría asociada y asigna al empleado organizador.
     * 
     * @param evento                El evento a guardar.
     * @param idEmpleadoOrganizador El ID del empleado que organiza el evento.
     */
    void guardarEvento(Evento evento, Long idEmpleadoOrganizador);

    /**
     * Obtiene la lista de eventos disponibles (con fecha futura).
     * 
     * @return Lista de eventos próximos.
     */
    List<Evento> listarEventosDisponibles();

}
