package backend.server.service.Repository;

import backend.server.service.domain.RessourceAccessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RessourceAccessorRepository extends JpaRepository<RessourceAccessor, Long> {
}