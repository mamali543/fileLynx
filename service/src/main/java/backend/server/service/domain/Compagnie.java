package backend.server.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Data
public class Compagnie extends RessourceAccessor{
    private String nom;
    private Double quota;
    private Double usedQuota;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie", fetch = FetchType.LAZY) @JsonIncludeProperties({"nom","quota","membres"})
    private List<Groupe> groupes = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    private List<Dossier> dossiers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    private List<Fichier> fichiers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie") @JsonIncludeProperties({"id", "message", "type", "date", "trigger"})
    private List<Log> logs = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    @JsonIncludeProperties({"id","nom"})
    private List<Categorie> categories = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    @JsonIncludeProperties({"id","nom"})
    private List<Label> labels = new ArrayList<>();

    @JsonIgnore
    public List<Groupe> getGroupes() {
        return groupes;
    }

    @Override
    public String toString() {
        return nom;
    }


}
