package backend.server.service.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class QuotaUsedToday {
    private Double quotaUsedToday;
    private Long fileCount;
}
