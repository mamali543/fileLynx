package backend.server.service.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @Data @NoArgsConstructor
public class EntitiesCountResponse {
    Long Membres;
    Long Groupes;
    Long Dossiers;
    Long Fichiers;
    Long Labels;
    Long Categories;
}
