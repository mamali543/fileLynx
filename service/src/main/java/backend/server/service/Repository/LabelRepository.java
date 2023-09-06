package backend.server.service.Repository;

import backend.server.service.domain.Categorie;
import backend.server.service.domain.Groupe;
import backend.server.service.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label,Long> {
    List<Label> findAllByCompagnieNom(String compagnieNom, org.springframework.data.domain.Sort sort);

    Label findByIdAndCompagnieNom(Long labelId, String compagnieNom);

    Label findByNom(String nom);
    Long countByCompagnieNom(String compagnieNom);
}
