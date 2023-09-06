package backend.server.service.domain;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"compagnie_id", "nom"}))
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categorie")
    private List<Fichier> fichiers = new ArrayList<>();
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIncludeProperties({"id","nom"})
    private Compagnie compagnie;

    public String toString(){
        String str = "\nCategorie: "+nom;
        return str;
    }
}
