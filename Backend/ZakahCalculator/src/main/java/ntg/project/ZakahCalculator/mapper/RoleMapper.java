package ntg.project.ZakahCalculator.mapper;

<<<<<<< HEAD
import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.util.UserType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default Role fromUserType(UserType userType) {
        if (userType == null) return null;

        return Role.builder()
                .name(userType)
                .build();
    }

    default UserType toUserType(Role role) {
        if (role == null) return null;
        return role.getName();
    }
=======
public interface RoleMapper {
>>>>>>> 014a1fd10945a19fe5b84da52a2dd6ccb772e5ba
}
