package backend.server.service.security.repositories;

import backend.server.service.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing users in the database
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username
     *
     * @param username the username to search for
     * @return the optional user with the given username
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username exists in the database
     *
     * @param username the username to check for
     * @return true if a user with the given username exists, false otherwise
     */
    Boolean existsByUsername(String username);

    void deleteByUsername(String username);

    /**
     * Checks if a user with the given email exists in the database
     *
     * @param email the email to check for
     * @return true if a user with the given email exists, false otherwise
     */
    Boolean existsByEmail(String email);
}