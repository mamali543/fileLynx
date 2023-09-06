package backend.server.service.Repository;

import backend.server.service.domain.Groupe;
import backend.server.service.domain.Membre;
import org.hibernate.metamodel.model.domain.internal.MapMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MembreRepository extends JpaRepository<Membre, Long> {
    Membre findByUsername(String username);
    List<Membre> findAllByCompagnieNom(String compagnieNom, org.springframework.data.domain.Sort sort);
    List<Membre> findAllByGroupeId(Long groupeId);
    Long countByCompagnieNom(String compagnieNom);

}
