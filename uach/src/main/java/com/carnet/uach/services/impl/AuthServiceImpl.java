package com.carnet.uach.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.carnet.uach.models.Usuario;
import com.carnet.uach.repositories.EmpleadoRepository;
import com.carnet.uach.repositories.EstudianteRepository;
import com.carnet.uach.repositories.UsuarioRepository;
import com.carnet.uach.services.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final EmpleadoRepository empleadoRepository;

    @Override
    public Usuario autenticarUsuario(String identificador, String contrasena) {
        Optional<? extends Usuario> usuarioOpt = Optional.empty();

        identificador = identificador.trim().toLowerCase();

        if (identificador.contains("@")) {
            // Es un correo electrónico
            usuarioOpt = usuarioRepository.findByCorreo(identificador);
        } else if (identificador.startsWith("a")) {
            // Es un estudiante (a + matricula)
            try {
                Long matricula = Long.valueOf(identificador.substring(1));
                usuarioOpt = estudianteRepository.findById(matricula);
            } catch (NumberFormatException e) {
                // Formato inválido
            }
        } else if (identificador.startsWith("e")) {
            // Es un empleado (e + id_empleado)
            try {
                Long idEmpleado = Long.valueOf(identificador.substring(1));
                usuarioOpt = empleadoRepository.findById(idEmpleado);
            } catch (NumberFormatException e) {
                // Formato inválido
            }
        }

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getContrasena().equals(contrasena)) {
                return usuario;
            }
        }
        return null;
    }
}
