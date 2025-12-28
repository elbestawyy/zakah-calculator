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
public class ZakahIndividualRecordSummaryResponse {

    private Long id;

    // Status
    private ZakahStatus status;
    private String statusDescription;

    // Summary calculations
    private BigDecimal totalAssets;
    private BigDecimal zakahAmount;

    // Dates for reference
    private LocalDate calculationDate;

    // User reference
    private Long userId;
}