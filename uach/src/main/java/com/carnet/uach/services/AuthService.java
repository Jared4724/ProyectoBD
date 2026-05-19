package com.carnet.uach.services;

import com.carnet.uach.models.Usuario;

public interface AuthService {
    /**
     * Autentica un usuario por correo y contraseña.
     * @param correo Correo electrónico del usuario
     * @param contrasena Contraseña del usuario
     * @return El objeto Usuario si la autenticación es exitosa, o null si falla.
     */
    Usuario autenticarUsuario(String correo, String contrasena);
}
