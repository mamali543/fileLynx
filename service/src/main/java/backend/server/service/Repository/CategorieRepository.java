package backend.server.service.Repository;

import backend.server.service.domain.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategorieRepository extends JpaRepository<Categorie,Long> {
    Categorie findByNom(String nom);
    Categorie findByIdAndCompagnieNom(Long id, String compagnieNom);
    List<Categorie> findAllByCompagnieNom(String compagnieNom, org.springframework.data.domain.Sort sort);
    Long countByCompagnieNom(String compagnieNom);
}
