package backend.server.service.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class Quota {
    private Double quota;
    private Double usedQuota;
    private Double quotaLeft;
}
