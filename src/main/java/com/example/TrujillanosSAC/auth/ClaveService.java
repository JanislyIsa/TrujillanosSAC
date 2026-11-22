package com.example.TrujillanosSAC.auth;

/** Recuperar la contraseña por correo y cambiarla desde "Mi cuenta". Solo clientes (son los que tienen correo). */
public interface ClaveService {

    /**
     * Si el correo es de un cliente, crea un enlace de un solo uso y lo envía por correo.
     * No informa si el correo existe: quien llama responde siempre el mismo mensaje.
     */
    void solicitarRestablecimiento(String correo);

    /** Cambia la clave con el token del enlace. Falla si el token no existe, ya se usó o venció. */
    void restablecer(String token, String nuevaClave);

    /** Cambio de clave de un usuario con sesión iniciada; exige la clave actual. */
    void cambiar(String username, String claveActual, String nuevaClave);
}
