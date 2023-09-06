package backend.server.service.domain;

import backend.server.service.enums.AuthLevel;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Authorisation {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JsonIncludeProperties({"id","nom"})
    private Dossier dossier;
    private boolean lecture;
    private boolean ecriture;
    private boolean modification;
    private boolean suppression;
    private boolean telechargement;
    private boolean upload;
    private boolean creationDossier;
    @Enumerated(EnumType.STRING)
    private AuthLevel authLevel;
    @ManyToOne
    @JsonIncludeProperties({"id"})
    @JoinColumn(name = "ressource_accessor_id")
    private RessourceAccessor ressourceAccessor;

    public static Authorisation generateFullAccess(){
        return Authorisation.builder()
                .lecture(true)
                .ecriture(true)
                .modification(true)
                .suppression(true)
                .telechargement(true)
                .upload(true)
                .creationDossier(true)
                .build();
    }

    public static Authorisation generateReadOnly(){
        return Authorisation.builder()
                .lecture(true)
                .ecriture(false)
                .modification(false)
                .suppression(false)
                .telechargement(false)
                .upload(false)
                .creationDossier(false)
                .build();
    }

    public static Authorisation generateNoAuths(){
        return Authorisation.builder()
                .lecture(false)
                .ecriture(false)
                .modification(false)
                .suppression(false)
                .telechargement(false)
                .upload(false)
                .creationDossier(false)
                .build();
    }

    private void checkLecture() {
        if (!this.lecture) {
            throw new RuntimeException("l'entité doit avoir le droit a la lecture pour avoir ce droit");
        }
    }

    public void setLecture(boolean lecture) {
        this.lecture = lecture;
    }

    public void setEcriture(boolean ecriture) {
        if(ecriture==true) checkLecture();
        this.ecriture = ecriture;
    }

    public void setModification(boolean modification) {
        if(modification) checkLecture();
        this.modification = modification;
    }

    public void setSuppression(boolean suppression) {
        if(suppression) checkLecture();
        this.suppression = suppression;
    }



    public void setTelechargement(boolean telechargement) {
        if(telechargement) checkLecture();
        this.telechargement = telechargement;
    }

    public void setUpload(boolean upload) {
        if(upload) checkLecture();
        this.upload = upload;
    }

    public void setCreationDossier(boolean creationDossier) {
        if(creationDossier) checkLecture();
        this.creationDossier = creationDossier;
    }
}
