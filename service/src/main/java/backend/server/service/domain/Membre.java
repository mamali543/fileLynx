package backend.server.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @AllArgsConstructor @NoArgsConstructor @Builder @Data
public class Membre extends RessourceAccessor{
    private String username;
    private String nom;
    private String prenom;
    private String email;
    @ManyToOne @JsonIncludeProperties("nom")
    private Groupe groupe;
    @ManyToOne @JsonIncludeProperties({"nom","id"})
    private Compagnie compagnie;


}
