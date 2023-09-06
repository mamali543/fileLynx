package backend.server.service.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data @AllArgsConstructor @NoArgsConstructor
public class FileResponse {
    private InputStreamResource file;
    private String fileName;
    private long fileSize;
}
