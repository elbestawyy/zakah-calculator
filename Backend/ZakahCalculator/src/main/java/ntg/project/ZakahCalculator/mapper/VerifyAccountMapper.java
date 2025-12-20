package ntg.project.ZakahCalculator.mapper;

import ntg.project.ZakahCalculator.dto.response.VerifyAccountResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VerifyAccountMapper {

    default VerifyAccountResponse successResponse() {
        return VerifyAccountResponse.builder()
                .message("Account verified successfully")
                .build();
    }
}
