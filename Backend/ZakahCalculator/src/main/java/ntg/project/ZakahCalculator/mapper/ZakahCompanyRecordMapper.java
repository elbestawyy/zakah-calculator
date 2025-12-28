package ntg.project.ZakahCalculator.mapper;

import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.repository.ZakahCompanyRecordRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ZakahCompanyRecordMapper {

    public ZakahCompanyRecord toEntity(ZakahCompanyRecordRequest request, User user) {
        if (request == null) {return null;}

        ZakahCompanyRecord record = new ZakahCompanyRecord();

        // Map all fields
        record.setCashEquivalents(request.getCashEquivalents() != null ? request.getCashEquivalents() : BigDecimal.ZERO);
        record.setAccountsReceivable(request.getAccountsReceivable() != null ? request.getAccountsReceivable() : BigDecimal.ZERO);
        record.setInventory(request.getInventory() != null ? request.getInventory() : BigDecimal.ZERO);
        record.setInvestment(request.getInvestment() != null ? request.getInvestment() : BigDecimal.ZERO);
        record.setAccountsPayable(request.getAccountsPayable() != null ? request.getAccountsPayable() : BigDecimal.ZERO);
        record.setShortTermLiability(request.getShortTermLiability() != null ? request.getShortTermLiability() : BigDecimal.ZERO);
        record.setAccruedExpenses(request.getAccruedExpenses() != null ? request.getAccruedExpenses() : BigDecimal.ZERO);
        record.setYearlyLongTermLiabilities(request.getYearly_long_term_liabilities() != null ? request.getYearly_long_term_liabilities() : BigDecimal.ZERO);
        record.setGoldPrice(request.getGoldPrice() != null ? request.getGoldPrice() : BigDecimal.ZERO);
        record.setUser(user);

        if (request.getBalance_sheet_date() != null) {
            record.setBalanceSheetDate(request.getBalance_sheet_date());
        }

        return record;
    }

    //Detailed Response
    public ZakahCompanyRecordResponse toDetailedResponse(ZakahCompanyRecord entity) {
        if (entity == null) {
            return null;
        }

        // Calculate totals
        BigDecimal totalAssets = nullToZero(entity.getCashEquivalents())
                .add(nullToZero(entity.getAccountsReceivable()))
                .add(nullToZero(entity.getInventory()))
                .add(nullToZero(entity.getInvestment()));

        BigDecimal totalLiabilities = nullToZero(entity.getAccountsPayable())
                .add(nullToZero(entity.getShortTermLiability()))
                .add(nullToZero(entity.getAccruedExpenses()))
                .add(nullToZero(entity.getYearlyLongTermLiabilities()));

        BigDecimal zakahPool = totalAssets.subtract(totalLiabilities);

        return ZakahCompanyRecordResponse.builder()
                .id(entity.getId())
                // Status information
                .status(entity.getStatus())
                .statusDescription(entity.getStatus().getDescription())
                // Assets
                .cashEquivalents(entity.getCashEquivalents())
                .accountsReceivable(entity.getAccountsReceivable())
                .inventory(entity.getInventory())
                .investment(entity.getInvestment())
                // Liabilities
                .accountsPayable(entity.getAccountsPayable())
                .shortTermLiability(entity.getShortTermLiability())
                .accruedExpenses(entity.getAccruedExpenses())
                .yearlyLongTermLiabilities(entity.getYearlyLongTermLiabilities())
                // Zakah Info
                .goldPrice(entity.getGoldPrice())
                .userId(entity.getUser().getId())
                .zakahAmount(entity.getZakahAmount())
                // Calculated values
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .currentZakahPool(zakahPool)
                .balanceSheetDate(entity.getBalanceSheetDate())
                .build();
    }


    //Summary Response
    public ZakahCompanyRecordSummaryResponse toSummaryResponse(ZakahCompanyRecord entity) {
        if (entity == null) {
            return null;
        }

        // Calculate totals
        BigDecimal totalAssets = nullToZero(entity.getCashEquivalents())
                .add(nullToZero(entity.getAccountsReceivable()))
                .add(nullToZero(entity.getInventory()))
                .add(nullToZero(entity.getInvestment()));

        BigDecimal totalLiabilities = nullToZero(entity.getAccountsPayable())
                .add(nullToZero(entity.getShortTermLiability()))
                .add(nullToZero(entity.getAccruedExpenses()))
                .add(nullToZero(entity.getYearlyLongTermLiabilities()));

        BigDecimal zakahPool = totalAssets.subtract(totalLiabilities);

        return ZakahCompanyRecordSummaryResponse.builder()
                //Basic Information
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .balanceSheetDate(entity.getBalanceSheetDate())
                // Status
                .status(entity.getStatus())
                .statusDescription(entity.getStatus().getDescription())

                // Summary calculations
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .zakahPool(zakahPool)
                .zakahAmount(entity.getZakahAmount())

                .build();
    }

    //convert null to zero
    private BigDecimal nullToZero(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO);
    }
}





