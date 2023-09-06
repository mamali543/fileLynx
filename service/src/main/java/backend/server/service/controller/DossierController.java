package backend.server.service.controller;


import backend.server.service.Literals;
import backend.server.service.Repository.LogRepository;
import backend.server.service.Service.*;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Log;
import backend.server.service.enums.LogType;
import backend.server.service.security.POJOs.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dossier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RequiredArgsConstructor
public class DossierController {

    private final IDossierService dossierService;
    private final CompagnieService compagnieService;
    private final LogRepository logRepository;
    private final GroupeService groupeService;
    private final AuthotisationService authotisationService;


    /**
     * Ajoute un dossier dans le dossier parent spécifié
     * @param d dossier à ajouter
     * @param parentFolderId id du dossier parent
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @PostMapping("/admin/add/{parentFolderId}")
    public ResponseEntity<?> addDossier(@RequestBody Dossier d, @PathVariable Long parentFolderId) {
        dossierService.addDossierCtrl(d, parentFolderId);
        return ResponseEntity.ok(new MessageResponse(Literals.FOLDER_CREATE_SUCCESS));

    }

    /**
     * Récupère le dossier racine de la compagnie
     * @return le dossier racine de la compagnie
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/getRoot")
    public Dossier getRoot() {
        return dossierService.getRootDossier();
    }

    /**
     * supprime de dossier spécifié
     * @param dossierId id du dossier à supprimer
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @DeleteMapping("/admin/delete/{dossierId}")
    public ResponseEntity<?> deleteDossier(@PathVariable Long dossierId) {
        dossierService.deleteDossierCtrl(dossierId);
        return ResponseEntity.ok(new MessageResponse(Literals.FOLDER_DELETE_SUCCESS));
    }

    /**
     * Renomme le dossier spécifié
     * @param dossierId id du dossier à renommer
     * @param name nouveau nom du dossier
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @PostMapping("/admin/rename/{dossierId}")
    public ResponseEntity<?> renameDossier(@PathVariable Long dossierId,@RequestParam String name) {
        dossierService.renameDossierCtrl(dossierId, name);
        return ResponseEntity.ok(new MessageResponse(Literals.FOLDER_EDIT_SUCCESS));
    }

    /**
     * change l'emplacement du dossier spécifié
     * @param dossierId id du dossier à déplacer
     * @param targetFolderId id du dossier cible
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/changerEmplacement/{dossierId}")
    public ResponseEntity<?> changerEmplacement(@PathVariable Long dossierId,@RequestParam Long targetFolderId) {
        dossierService.changerEmplacement(dossierId, targetFolderId);
        return ResponseEntity.ok(new MessageResponse(Literals.FOLDER_EDIT_SUCCESS));
    }

    /**
     * Récupère les dossiers enfants du dossier spécifié
     * @param dossierId id du dossier parent
     * @return la liste des dossiers enfants
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/getChildrenFolders/{dossierId}")
    public List<Dossier> getChildrenDossiers(@PathVariable Long dossierId) {
        return dossierService.getChildrenDossiers(dossierId);
    }

    /**
     * Récupère le dossier spécifié
     * @param dossierId id du dossier à récupérer
     * @return le dossier spécifié
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping("/admin/get/{dossierId}")
    public Dossier getDossier(@PathVariable Long dossierId) {
        return dossierService.getDossier(dossierId);
    }
}
