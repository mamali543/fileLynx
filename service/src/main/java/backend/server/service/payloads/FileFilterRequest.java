package backend.server.service.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class FileFilterRequest {
    private List<String> labels;
    private String category;
    private String query;
}
