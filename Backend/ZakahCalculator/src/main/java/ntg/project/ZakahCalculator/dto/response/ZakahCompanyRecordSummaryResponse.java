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
    private Long userId;

    // Status
    private ZakahStatus status;
    private String statusDescription;

    // Summary calculations
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal zakahPool;
    private BigDecimal zakahAmount;


}
