package backend.server.service.controller;

import backend.server.service.Service.DossierService;
import backend.server.service.Service.IAuthotisationService;
import backend.server.service.domain.Dossier;
import backend.server.service.security.POJOs.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RequiredArgsConstructor
public class AuthTestController {

    private final IAuthotisationService authotisationService;
    private final DossierService dossierService;
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @PostMapping("/checkAuth")
    public ResponseEntity<?> String() {
        Long ressourceAccessorid = authotisationService.extractResourceAssessorIdFromSecurityContext();
        return ResponseEntity.ok(new MessageResponse(ressourceAccessorid.toString()));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @PostMapping("/addFolder/{dossierId}")
    public ResponseEntity<?> addFolder(@RequestBody Dossier f, @PathVariable Long dossierId) {
        Long resourceAccessorid = authotisationService.extractResourceAssessorIdFromSecurityContext();
        authotisationService.authorize(resourceAccessorid, dossierId, "creationDossier");
        dossierService.addDossier(f, dossierId, authotisationService.extractCompagnieFromResourceAccessor(),false);
        authotisationService.generateDefaultAuths(resourceAccessorid, f);
        return ResponseEntity.ok(new MessageResponse("fichier ajouté avec succès!"));
    }

}
