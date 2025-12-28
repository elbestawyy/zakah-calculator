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
public class ZakahIndividualRecordResponse {

    private Long id;

    // Status information
    private ZakahStatus status;

    // Individual Assets
    private BigDecimal cash;
    private BigDecimal gold;
    private BigDecimal silver;
    private BigDecimal stocks;
    private BigDecimal bonds;

    // Zakah Info
    private BigDecimal goldPrice;

    // Current Record - Main Display
    private BigDecimal totalAssets;
    private BigDecimal zakahAmount;
    private LocalDate calculationDate;

}