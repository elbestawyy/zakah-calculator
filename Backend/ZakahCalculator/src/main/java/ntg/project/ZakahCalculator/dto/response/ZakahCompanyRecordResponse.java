package ntg.project.ZakahCalculator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZakahCompanyRecordResponse {

    private Long id;
    private ZakahStatus status;
    // Assets
    private BigDecimal cashEquivalents;
    private BigDecimal investment;
    private BigDecimal inventory;
    private BigDecimal accountsReceivable;

    // Liabilities
    private BigDecimal accountsPayable;
    private BigDecimal accruedExpenses;
    private BigDecimal shortTermLiability;
    private BigDecimal yearlyLongTermLiabilities;

    // Zakah Info
    private BigDecimal goldPrice;

    // Current Record - Main Display
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal currentZakahPool; // Net wealth (Assets - Liabilities)
    private BigDecimal zakahAmount;
    private BigDecimal nisabAmount;
    private LocalDate balanceSheetDate;
}