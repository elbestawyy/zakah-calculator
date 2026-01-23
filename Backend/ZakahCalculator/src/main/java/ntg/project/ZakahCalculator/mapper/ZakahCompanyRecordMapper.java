package ntg.project.ZakahCalculator.mapper;

import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import org.springframework.stereotype.Component;

@Component
public class ZakahCompanyRecordMapper {

    public ZakahCompanyRecord toEntity(
            ZakahCompanyRecordRequest request,
            User user) {

        ZakahCompanyRecord record = new ZakahCompanyRecord();
        record.setBalanceSheetDate(request.getBalanceSheetDate());

        // Assets
        record.setCashEquivalents(request.getCashEquivalents());
        record.setAccountsReceivable(request.getAccountsReceivable());
        record.setInventory(request.getInventory());
        record.setInvestment(request.getInvestment());
        record.setGeneratingFixedAssets(request.getGeneratingFixedAssets());

        // Liabilities
        record.setAccountsPayable(request.getAccountsPayable());
        record.setShortTermLiability(request.getShortTermLiability());
        record.setAccruedExpenses(request.getAccruedExpenses());
        record.setYearlyLongTermLiabilities(
                request.getYearlyLongTermLiabilities()
        );
        record.setContraAssets(request.getContraAssets());
        record.setProvisionsUnderLiabilities(request.getProvisionsUnderLiabilities());
        record.setGoldPrice(request.getGoldPrice());
        record.setUser(user);
        return record;
    }

    public ZakahCompanyRecordSummaryResponse toSummaryResponse(
            ZakahCompanyRecord entity) {

        return ZakahCompanyRecordSummaryResponse.builder()
                .id(entity.getId())
                .balanceSheetDate(entity.getBalanceSheetDate())
                .status(entity.getStatus())
                .zakahAmount(entity.getZakahAmount())
                .build();
    }

    public ZakahCompanyRecordResponse toDetailedResponse(
            ZakahCompanyRecord entity) {

        return ZakahCompanyRecordResponse.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .cashEquivalents(entity.getCashEquivalents())
                .accountsReceivable(entity.getAccountsReceivable())
                .inventory(entity.getInventory())
                .investment(entity.getInvestment())
                .generatingFixedAssets(entity.getGeneratingFixedAssets())
                .accountsPayable(entity.getAccountsPayable())
                .shortTermLiability(entity.getShortTermLiability())
                .accruedExpenses(entity.getAccruedExpenses())
                .yearlyLongTermLiabilities(entity.getYearlyLongTermLiabilities())
                .contraAssets(entity.getContraAssets())
                .provisionsUnderLiabilities(entity.getProvisionsUnderLiabilities())
                .goldPrice(entity.getGoldPrice())
                .balanceSheetDate(entity.getBalanceSheetDate())
                .totalAssets(entity.getTotalAssets())
                .totalLiabilities(entity.getTotalLiabilities())
                .zakahPool(entity.getZakahPool())
                .zakahAmount(entity.getZakahAmount())
                .build();
    }
}
