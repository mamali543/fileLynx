package backend.server.service.domain;

import backend.server.service.enums.LogType;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String message;
    @Enumerated(EnumType.STRING)
    private LogType type;
    private Date date;
    @ManyToOne (cascade = CascadeType.ALL) @JsonIncludeProperties({"nom"})
    private RessourceAccessor trigger;
    @ManyToOne (cascade = CascadeType.ALL) @JsonIncludeProperties({"nom"})
    private Compagnie compagnie;
}
