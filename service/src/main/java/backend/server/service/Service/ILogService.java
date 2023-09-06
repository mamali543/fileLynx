package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.Log;

public interface ILogService {
    PageResponse<Log> getLogsPage(int page, int size, String sortBy);
}
