package backend.server.service.security.POJOs.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simple data class representing a message response returned by the server.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    /**
     * The message included in the response.
     */
    private String message;
}
