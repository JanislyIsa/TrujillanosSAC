package com.example.TrujillanosSAC.Config;

import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import com.example.TrujillanosSAC.usuarios.model.Rol;
import com.example.TrujillanosSAC.usuarios.model.TipoDocumento;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.PersonaRepository;
import com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository;
import com.example.TrujillanosSAC.usuarios.repository.RolRepository;
import com.example.TrujillanosSAC.usuarios.repository.TipoDocumentoRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="app.seed.habilitado", havingValue="true", matchIfMissing=true)
@Configuration
public class ProveedorUserSeeder {

    @Bean
    @Transactional
    CommandLineRunner crearUsuarioProveedor(UsuarioRepository usuarioRepository,
                                            RolRepository rolRepository,
                                            TipoDocumentoRepository tipoDocumentoRepository,
                                            PersonaRepository personaRepository,
                                            ProveedorRepository proveedorRepository,
                                            PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.existsByUsername("proveedor")) {
                return;
            }

            Rol rolProveedor = rolRepository.findByNombreIgnoreCase("PROVEEDOR")
                    .orElseGet(() -> rolRepository.save(Rol.builder().nombre("PROVEEDOR").build()));

            TipoDocumento ruc = tipoDocumentoRepository.findByAbreviaturaIgnoreCase("RUC")
                    .orElseGet(() -> tipoDocumentoRepository.save(TipoDocumento.builder()
                            .nombre("Registro Unico de Contribuyentes")
                            .abreviatura("RUC")
                            .longitud(11)
                            .build()));

            Persona persona = personaRepository.findByNumDoc("20600000001")
                    .orElseGet(() -> personaRepository.save(Persona.builder()
                            .nombre("Distribuidora")
                            .apellido("Altiplano S.A.")
                            .telefono("988888888")
                            .documento(ruc)
                            .numDoc("20600000001")
                            .build()));

            if (!proveedorRepository.existsById(persona.getIdPersona())) {
                proveedorRepository.save(Proveedor.builder()
                        .persona(persona)
                        .correo("proveedor@eltrujillano.com")
                        .direccion("Av. Proveedores 120")
                        .build());
            }

            usuarioRepository.save(Usuario.builder()
                    .username("proveedor")
                    .password(passwordEncoder.encode("proveedor123"))
                    .persona(persona)
                    .roles(Set.of(rolProveedor))
                    .build());
        };
    }
}
