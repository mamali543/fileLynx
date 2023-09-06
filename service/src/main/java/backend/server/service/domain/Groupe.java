package backend.server.service.domain;

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

@Entity @AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties({"authorisations","logs"})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"compagnie_id", "nom"}))
public class Groupe extends RessourceAccessor{
    private String nom;
    private Double quota;
    private Double quotaUsed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Compagnie compagnie;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupe") @JsonIncludeProperties({"nom","prenom","username","email"})
    private List<Membre> membres = new ArrayList<>();
    @JsonIgnore
    public Compagnie getCompagnie() {
        return compagnie;
    }
    @Override
    public String toString() {
        return nom;
    }
}
