package backend.server.service.Service;

import backend.server.service.POJO.Quota;
import org.springframework.http.ResponseEntity;

public interface IQuotaService {
    Quota getQuotaStatus();
    Double getCompagnieUnallocatedQuota();

}
