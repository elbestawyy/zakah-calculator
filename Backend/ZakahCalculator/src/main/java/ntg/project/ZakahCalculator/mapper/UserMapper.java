package ntg.project.ZakahCalculator.mapper;

<<<<<<< HEAD
import ntg.project.ZakahCalculator.dto.request.ProfileUpdateRequest;
import ntg.project.ZakahCalculator.dto.request.RegistrationRequest;
import ntg.project.ZakahCalculator.dto.response.UserResponse;
import ntg.project.ZakahCalculator.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /* ================= Registration ================= */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", expression = "java(request.getFirstName() + \" \" + request.getLastName())")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "deleted", constant = "false")
    User toEntity(RegistrationRequest request);

    /* ================= Profile Update ================= */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name",
            expression = "java((request.getFirstName() != null || request.getLastName() != null) ? " +
                    "(request.getFirstName() != null ? request.getFirstName() : \"\") + \" \" + " +
                    "(request.getLastName() != null ? request.getLastName() : \"\") : user.getName())")
    void updateUserFromRequest(ProfileUpdateRequest request, @MappingTarget User user);

    /* ================= Entity → Response ================= */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "fullName", source = "name")
    @Mapping(target = "userType",
            expression = "java(user.getRoles().get(0).getName())")
    UserResponse toResponse(User user);
=======
public interface UserMapper {
>>>>>>> 014a1fd10945a19fe5b84da52a2dd6ccb772e5ba
}
