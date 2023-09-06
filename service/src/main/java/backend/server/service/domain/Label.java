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
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    @ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "labels")
    @JsonIncludeProperties({"id","nom","taille","extension"})
    private List<Fichier> fichiers = new ArrayList<>();
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIncludeProperties({"id","nom"})
    private Compagnie compagnie;

    public String toString(){
        String str = "\nLabel: "+nom;
        return str;
    }
}
