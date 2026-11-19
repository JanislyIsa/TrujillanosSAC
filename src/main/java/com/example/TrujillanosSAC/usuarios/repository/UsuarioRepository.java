package com.example.TrujillanosSAC.usuarios.repository;

import com.example.TrujillanosSAC.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("select u from Usuario u left join fetch u.roles where u.username = :username")
    Optional<Usuario> findByUsernameConRoles(@Param("username") String username);

    @Query("select u from Usuario u left join fetch u.roles left join fetch u.persona where u.username = :username")
    Optional<Usuario> findByUsernameConRolesYPersona(@Param("username") String username);

    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdUsuarioNot(String username, Long idUsuario);
    boolean existsByPersonaIdPersona(Long personaId);
    Optional<Usuario> findByPersonaIdPersona(Long personaId);
    boolean existsByRolesIdRol(Long rolId);
}
