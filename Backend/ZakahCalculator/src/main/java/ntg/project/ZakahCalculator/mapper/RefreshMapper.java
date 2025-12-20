package ntg.project.ZakahCalculator.mapper;

import ntg.project.ZakahCalculator.dto.response.AuthenticationResponse;
import ntg.project.ZakahCalculator.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshMapper {

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "userResponse", expression = "java(null)")
    AuthenticationResponse toResponse(String accessToken, String refreshToken);
}
