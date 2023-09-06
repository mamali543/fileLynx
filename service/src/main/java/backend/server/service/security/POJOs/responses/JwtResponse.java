package backend.server.service.security.POJOs.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents a response containing a JSON Web Token (JWT) and user information.
 * It is used to send the token and user data to the client after a successful login or token refresh.
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    /**
     * Constructs a new JwtResponse with the specified token, user information, and roles.
     *
     * @param accessToken the JWT access token
     * @param id          the user's ID
     * @param username    the user's username
     * @param email       the user's email address
     * @param roles       the user's roles as a list of strings
     **/
    public JwtResponse(String accessToken,String refreshToken , Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }


}
