package backend.server.service.Repository;

import backend.server.service.domain.Groupe;
import backend.server.service.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log,Long> {
    List<Log> findAllByCompagnieNom(String compagnieNom, org.springframework.data.domain.Sort sort);

}
