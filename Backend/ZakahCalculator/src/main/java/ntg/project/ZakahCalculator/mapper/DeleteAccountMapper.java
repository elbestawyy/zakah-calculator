package ntg.project.ZakahCalculator.mapper;

import ntg.project.ZakahCalculator.dto.response.DeleteAccountResponse;
import org.mapstruct.Mapper;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface DeleteAccountMapper {

    default DeleteAccountResponse toResponse(LocalDate deletedAt, LocalDate restoreUntil) {
        return DeleteAccountResponse.builder()
                .message("Account deleted successfully")
                .deletedAt(deletedAt)
                .restoreUntil(restoreUntil)
                .build();
    }
}
