package backend.server.service.controller;

import backend.server.service.Repository.*;
import backend.server.service.Service.*;
import backend.server.service.domain.Authorisation;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Membre;
import backend.server.service.payloads.CurrentAuth;
import backend.server.service.payloads.FileFilterRequest;
import backend.server.service.security.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RequiredArgsConstructor
@Slf4j
public class MembreController {
    private final ICompagnieService compagnieService;
    private final CompagnieRepository compagnieRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final IGroupeService groupeService;
    private final GroupeRepository groupeRepository;
    private final IMembreService membreService;
    private final LogRepository logRepository;
    private final CategorieRepository categorieRepository;
    private final LabelRepository labelRepository;
    private final ILogService logService;
    private final ICategorieService categorieService;
    private final ILabelService labelService;
    private final IDossierService dossierService;
    private final QuotaService quotaService;
    private final IAuthotisationService authotisationService;
    private final IFichierService fichierService;


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/getRoot")
    public Dossier getRoot() {
        Membre membre = membreService.getMembre(authotisationService.extractResourceAccessorFromSecurityContext().getId());
        return dossierService.getGroupRootForUser(membre.getGroupe());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/getFilteredFiles")
    public List<Fichier> getFilteredFiles(@RequestBody FileFilterRequest fileFilterRequest) {
        return fichierService.getFilteredFiles(fileFilterRequest);
    }



    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/getDossier/{id}")
    public Dossier getDossier(@PathVariable Long id) {
        Membre membre = membreService.getMembre(authotisationService.extractResourceAccessorFromSecurityContext().getId());
        Dossier dossier = dossierService.getDossierByIdAsUser(id);
        Authorisation authorisation = authotisationService.getAuthorisation(membre.getId(), dossier.getId());
        dossier.setCurrentAuth(new CurrentAuth(authorisation));
        return dossierService.getDossierByIdAsUser(id);
    }
}
