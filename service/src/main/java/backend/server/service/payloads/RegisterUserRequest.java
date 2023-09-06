package backend.server.service.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data
public class RegisterUserRequest {
    String username;
    String nom;
    String prenom;
    String email;
    String Groupe;
    String password;
}
