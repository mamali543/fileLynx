package backend.server.service.Repository;

import backend.server.service.domain.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FichierRepository extends JpaRepository<Fichier,Long> {
    Long countByCompagnieNom(String compagnieNom);
    //count by compagnie nom and dateCreation is today
    List<Fichier> findAllByCompagnieNom(String compagnieNom);
    Long countByCompagnieNomAndDateCreation(String compagnieNom, Date dateCreation);
    //get all Fichiers by compagnie nom and dateCreation
    List<Fichier> findAllByCompagnieNomAndDateCreation(String compagnieNom, Date dateCreation);


}
