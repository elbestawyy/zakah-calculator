package ntg.project.ZakahCalculator.mapper;

import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.dto.request.ZakahIndividualRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ZakahIndividualRecordMapper {

     //Convert request DTO to entity
    public ZakahIndividualRecord toEntity(ZakahIndividualRecordRequest request, User user) {
        if (request == null) {
            return null;
        }

        ZakahIndividualRecord record = new ZakahIndividualRecord();

        // Map all financial fields
        record.setCash(nullToZero(request.getCash()));
        record.setGold(nullToZero(request.getGold()));
        record.setSilver(nullToZero(request.getSilver()));
        record.setStocks(nullToZero(request.getStocks()));
        record.setBonds(nullToZero(request.getBonds()));
        record.setGoldPrice(nullToZero(request.getGoldPrice()));
        record.setUser(user);

        return record;
    }


     //entity to DETAILED response DTO
    public ZakahIndividualRecordResponse toDetailedResponse(ZakahIndividualRecord entity) {
        if (entity == null) {
            return null;
        }

        // Calculate total assets
        BigDecimal totalAssets = nullToZero(entity.getCash())
                .add(nullToZero(entity.getGold()))
                .add(nullToZero(entity.getSilver()))
                .add(nullToZero(entity.getStocks()))
                .add(nullToZero(entity.getBonds()));

        return ZakahIndividualRecordResponse.builder()
                .id(entity.getId())
                // Status information
                .status(entity.getStatus())
                .statusDescription(entity.getStatus().getDescription())
                // Assets
                .cash(entity.getCash())
                .gold(entity.getGold())
                .silver(entity.getSilver())
                .stocks(entity.getStocks())
                .bonds(entity.getBonds())
                // Zakah Info
                .goldPrice(entity.getGoldPrice())
                .userId(entity.getUser().getId())
                .zakahAmount(entity.getZakahAmount())
                // Calculated values
                .totalAssets(totalAssets)
                .zakahPool(totalAssets) // For individuals, zakah pool = total assets
                .calculationDate(entity.getCalculationDate())
                .build();
    }


     //entity to SUMMARY response DTO
    public ZakahIndividualRecordSummaryResponse toSummaryResponse(ZakahIndividualRecord entity) {
        if (entity == null) {
            return null;
        }

        // Calculate total assets
        BigDecimal totalAssets = nullToZero(entity.getCash())
                .add(nullToZero(entity.getGold()))
                .add(nullToZero(entity.getSilver()))
                .add(nullToZero(entity.getStocks()))
                .add(nullToZero(entity.getBonds()));

        return ZakahIndividualRecordSummaryResponse.builder()
                .id(entity.getId())
                // Status
                .status(entity.getStatus())
                .statusDescription(entity.getStatus().getDescription())
                // Summary calculations
                .totalAssets(totalAssets)
                .zakahAmount(entity.getZakahAmount())
                // Dates
                .calculationDate(entity.getCalculationDate())
                // User
                .userId(entity.getUser().getId())
                .build();
    }

    //converts null to zero
    private BigDecimal nullToZero(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO);
    }
}