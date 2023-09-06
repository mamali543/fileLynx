package backend.server.service.payloads;

import backend.server.service.domain.Authorisation;
import backend.server.service.domain.Dossier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.mail.Folder;

@Data @AllArgsConstructor @NoArgsConstructor
public class CurrentAuth {
    private boolean lecture;
    private boolean ecriture;
    private boolean modification;
    private boolean suppression;
    private boolean telechargement;
    private boolean upload;
    private boolean creationDossier;
    public CurrentAuth(Authorisation auth){
        this.lecture = auth.isLecture();
        this.ecriture = auth.isEcriture();
        this.modification = auth.isModification();
        this.suppression = auth.isSuppression();
        this.telechargement = auth.isTelechargement();
        this.upload = auth.isUpload();
        this.creationDossier = auth.isCreationDossier();
    }
}
