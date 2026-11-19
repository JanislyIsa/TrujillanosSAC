package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.auth.dto.LoginRequestDTO;
import com.example.TrujillanosSAC.auth.dto.LoginResponseDTO;

public interface AuthService {
    /** Lanza AuthenticationException si el usuario o la contraseña no son válidos. */
    LoginResponseDTO login(LoginRequestDTO request);
}
