package backend.server.service.Service;

import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Groupe;
import backend.server.service.domain.Membre;
import backend.server.service.payloads.FileFilterRequest;

import java.util.List;

public interface IDossierService {
    Dossier addDossier(Dossier d, Long ParentFolderId);
    Dossier addDossier(Dossier d, Long ParentFolderId, Compagnie compagnie, boolean skipAuthCreation);
    Dossier renameDossier(Long DossierId,String name);
    void fileTree(Long DossierId, Long level);
    void fileTreeFiles(Long fichierId, Long level);
    void delete(Long DossierId);
    Dossier changerEmplacement(Long dossierId,Long dossierCibleId );
    List<Dossier> getChildrenDossiers(Long dossierId);
    Dossier getDossier(Long dossierId);
    Dossier getRootDossier();
    Groupe getGroupRootGroupe(Long dossierId);
    Dossier getGroupRoot(Groupe groupe);

    Dossier addDossierCtrl(Dossier d, Long parentFolderId);

    void deleteDossierCtrl(Long dossierId);

    Dossier renameDossierCtrl(Long dossierId, String name);

    Dossier getGroupRootForUser(Groupe groupe);

    Dossier getDossierByIdAsUser(Long id);


}
