package backend.server.service.security.repositories;

import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository interface for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Retrieves an optional role based on its name.
     *
     * @param name the name of the role
     * @return an optional Role object
     */
    Optional<Role> findByName(EROLE name);
    Optional<Role> findByName(String name);
}