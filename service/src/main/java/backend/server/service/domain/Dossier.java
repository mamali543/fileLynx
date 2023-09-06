package backend.server.service.domain;

import backend.server.service.payloads.CurrentAuth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @AllArgsConstructor @Data @NoArgsConstructor @Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"racine_id", "nom"}))
public class Dossier {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    @ManyToOne
    @JsonIncludeProperties({"id","nom"})
    private Dossier racine;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "racine")
    @JsonIncludeProperties({"id","nom","extension","type","taille","etat"})
    private List<Fichier> fichiers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "racine")
    @JsonIncludeProperties({"id","nom","groupRoot","currentAuth"})
    private List<Dossier> dossiers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL , mappedBy = "dossier")
    private List<Authorisation> authorisations = new ArrayList<>();
    @ManyToOne
    @JsonIncludeProperties({"id","nom"})
    private Compagnie compagnie;
    @ManyToOne
    @JsonIncludeProperties({"id","nom"})
    private Groupe groupe;
    private boolean isGroupRoot;
    @Transient
    private CurrentAuth currentAuth;
    public String getFullPath(){
        String path= "/"+nom;
        if(racine != null) path = racine.getFullPath() + path;
        return path;
    }
    @Override
    public String toString() {
        return nom;
    }

}
