package backend.server.service.Service;

import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Label;
import backend.server.service.enums.ETAT;
import backend.server.service.payloads.FileFilterRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFichierService {

    Fichier addFichier(Fichier f, Long ParentFolderId);
    void deleteFichier(Long fichierId);
    Fichier rename(Long fichierId, String name);
    Fichier updateFichier(Fichier f);
    Fichier changerEmplacement(Long fichierId,Long dossierCibleId );
    Fichier getFichier(Long id);
    Fichier updateEtat(Long fichierId, ETAT etat);
    Fichier changeCategory(Long fichierId,Long categorieId);
    Fichier updateLabels(Long fichierId, List<Label> labels);
    List<Fichier> getFichiersByParent(Long parentId);

    List<String> uploadFile(MultipartFile file, Long folderId, List<String> selectedLabels, String selectedCategorie) throws Exception;

    ByteArrayResource dowloadFile(String name);

    void updateFile(String fileName, List<String> selectedLabels, String selectedCategorie, Long fileId);

    List<Fichier> getFilteredFiles(FileFilterRequest fileFilterRequest);
}
