package backend.server.service.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CustomFileUploadRequest {

    private MultipartFile file;
    private MultipartFile formData;
}
