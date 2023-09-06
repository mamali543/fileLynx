package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.CategorieRepository;
import backend.server.service.Repository.LogRepository;
import backend.server.service.domain.Categorie;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Log;
import backend.server.service.enums.LogType;
import backend.server.service.security.POJOs.responses.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @Slf4j @Transactional
public class CategorieService implements ICategorieService{

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private ICompagnieService compagnieService;
    @Autowired
    private LogRepository logRepository;

    /**
     * ajoute et persiste une nouvelle catégorie
     * @param cat catégorie à ajouter
     * @return la catégorie ajoutée
     */
    public Categorie addCategorie(Categorie cat)
    {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieName);

            cat.setCompagnie(compagnie);
            cat = categorieRepository.save(cat);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Catégorie " + cat.getNom() + " ajoutée.").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return cat;
    }

    /**
     * Supprime une catégorie
     * @param categorieId id de la catégorie à supprimer
     */
    public void deleteCategorie(Long categorieId)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Categorie categorie = categorieRepository.findByIdAndCompagnieNom(categorieId, compagnieNom);
        String categorieName = categorie.getNom();
        categorieRepository.deleteById(categorieId);
        Log logMessage = Log.builder().message("Catégorie " + categorieName + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);

    }

    /**
     * Met à jour une catégorie
     * @param cat catégorie à mettre à jour
     * @return la catégorie mise à jour
     */
    public Categorie updateCategorie(Categorie cat)
    {
        return categorieRepository.save(cat);
    }

    /**
     * Met à jour le nom d'une catégorie
     * @param categorieId id de la catégorie à mettre à jour
     * @param newName nouveau nom de la catégorie
     * @return la catégorie mise à jour
     */
    public Categorie updateCategorie(Long categorieId, String newName)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Categorie categorie = categorieRepository.findByIdAndCompagnieNom(categorieId, compagnieNom);
        String categorieName = categorie.getNom();
        Optional<Categorie> optCat = categorieRepository.findById(categorieId);
        if(optCat.isEmpty())
            throw new RuntimeException(Literals.CATEGORY_NOT_FOUND);
        Categorie cat = optCat.get();
        cat.setNom(newName);
        categorieRepository.save(cat);
        Log logMessage = Log.builder().message("Catégorie " + categorieName + " de la Société " + compagnieNom + " a été mis à jour").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return cat;
    }

    /**
     * Récupère toutes les catégories
     * @return la liste de toutes les catégories
     */
    public List<Categorie> getAllCategories()
    {
        return categorieRepository.findAll();
    }
    /** Récupère une catégorie par son id
     * @param id id de la catégorie à récupérer
     * @return la catégorie récupérée
     */
    public Categorie getCategorie(Long id)
    {
        return categorieRepository.findById(id).orElseThrow(()-> new RuntimeException(Literals.CATEGORY_NOT_FOUND));
    }
    /**
     * Récupère une catégorie par son nom
     */
    public Categorie getCategorie(String nom)
    {

        try
        {
            return categorieRepository.findByNom(nom);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Récupére un page de catégories de la base de données
     * @param page numéro de la page
     * @param size taille de la page
     * @param sortBy nom de la colonne à trier
     * @param sortOrder ordre de tri
     * @param searchQuery mot clé de recherche
     * @return la page de catégories
     */
    @Override
    public PageResponse<Categorie> getCategoriesPage(int page, int size, String sortBy, String sortOrder, String searchQuery) {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) categorieRepository.count());
        List<Categorie> categories = categorieRepository.findAllByCompagnieNom(compagnieName,sort);
        if (searchQuery != null && !searchQuery.isEmpty()){
            categories = categories.stream()
                    .filter(categorie -> categorie.getNom().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<Categorie> pageContent = categories.subList(start, Math.min(end, categories.size()));
        return new PageResponse<>(pageContent, categories.size());
    }
}
