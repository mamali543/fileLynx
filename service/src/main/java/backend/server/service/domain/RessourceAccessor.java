package backend.server.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class RessourceAccessor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany (cascade = CascadeType.ALL,mappedBy = "ressourceAccessor")
    private List<Authorisation> authorisations = new ArrayList<>();

    @OneToMany (cascade = CascadeType.ALL,mappedBy = "trigger") @JsonIncludeProperties({"id", "message", "type", "date", "trigger"})
    private List<Log> logs = new ArrayList<>();

}