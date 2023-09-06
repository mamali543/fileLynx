package backend.server.service.Repository;

import backend.server.service.domain.Compagnie;
import org.springframework.data.jpa.repository.JpaRepository;

// This is a CompagnieRepository that handles the query operations on the database
//codegpt do : make unit tests for this two methods in the repository and don't forget to comment them out
public interface CompagnieRepository extends JpaRepository<Compagnie, Long> {
    Compagnie findByNom(String nom);
    void deleteByNom(String nom);
}
