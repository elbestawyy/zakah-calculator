package ntg.project.ZakahCalculator.dto.response;

import lombok.*;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZakahCompanyRecordSummaryResponse {
    //Basic Information
    private Long id;
    private LocalDate balanceSheetDate;
    private ZakahStatus status;
    private BigDecimal zakahAmount;


}
