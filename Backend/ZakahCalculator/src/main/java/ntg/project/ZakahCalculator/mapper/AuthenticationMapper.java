package ntg.project.ZakahCalculator.mapper;

import ntg.project.ZakahCalculator.dto.response.AuthenticationResponse;
import ntg.project.ZakahCalculator.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface AuthenticationMapper {

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "userResponse", source = "user")
    AuthenticationResponse toResponse(String accessToken, String refreshToken, User user);
}
