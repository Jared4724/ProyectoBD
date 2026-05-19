package com.carnet.uach.services.impl;

import com.carnet.uach.models.Usuario;
import com.carnet.uach.repositories.UsuarioRepository;
import com.carnet.uach.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario autenticarUsuario(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // TODO: Implementar encriptación de contraseñas con BCrypt (ej. passwordEncoder.matches(...))
            if (usuario.getContrasena().equals(contrasena)) {
                return usuario;
            }
        }
        return null;
    }
}
