package backend.server.service.security.POJOs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class TokenRefreshRequest {
    private String refreshToken;
}
