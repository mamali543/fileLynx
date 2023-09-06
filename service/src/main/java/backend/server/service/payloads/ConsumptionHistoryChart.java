package backend.server.service.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data
public class ConsumptionHistoryChart {
    List<Double> consumptionHistory;
    List<Date> Labels;
}
