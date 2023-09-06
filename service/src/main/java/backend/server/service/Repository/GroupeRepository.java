package backend.server.service.Repository;

import backend.server.service.domain.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    // find a group by its name and the name of the company it belongs to
    Groupe findByNomAndCompagnieNom(String nom, String compagnieNom);
    // calculate the sum of quotas for all groups of a company
    @Query("SELECT COALESCE(SUM(g.quota), 0) FROM Groupe g WHERE g.compagnie.nom = :compagnieNom")
    Double sumQuotasByCompagnieNom(@Param("compagnieNom") String compagnieNom);
    Groupe findByNom(String nom);
    Groupe findByIdAndCompagnieNom(Long id, String compagnieNom);
    @Query("SELECT DISTINCT g.nom from Groupe g WHERE g.compagnie.nom = :compagnieNom")
    List<String> findAllUniqueGroupes(@Param("compagnieNom") String compagnieNom);
    //find all groups of a company, accepts a Sort object to sort the results
    List<Groupe> findAllByCompagnieNom(String compagnieNom, org.springframework.data.domain.Sort sort);
    List<Groupe> findAllByCompagnieNom(String compagnieNom);
    //count by company nom
    Long countByCompagnieNom(String compagnieNom);

}
