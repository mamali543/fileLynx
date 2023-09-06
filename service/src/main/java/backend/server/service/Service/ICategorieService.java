package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.Categorie;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICategorieService {
    public Categorie addCategorie(Categorie cat);
    public void deleteCategorie(Long categorieId);
    public Categorie updateCategorie(Long categorieId, String newName);
    public List<Categorie> getAllCategories();
    public Categorie getCategorie(Long id);
    public Categorie getCategorie(String nom);
    PageResponse<Categorie> getCategoriesPage(int page, int size, String sortBy, String sortOrder, String searchQuery );

}