package backend.server.service.security.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    /**
     * The ID of the role, generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    /**
     * The name of the role, an enumerated value.
     */
    @Enumerated(STRING)
    @Column(length = 20, unique = true)
    private EROLE name;
}