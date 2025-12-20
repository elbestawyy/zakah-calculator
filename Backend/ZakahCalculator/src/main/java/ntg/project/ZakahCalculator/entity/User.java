package ntg.project.ZakahCalculator.entity;

import jakarta.persistence.*;
import lombok.*;
import ntg.project.ZakahCalculator.entity.util.BaseEntity;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
<<<<<<< HEAD
public class User extends BaseEntity implements UserDetails {
=======
public class User extends BaseEntity {
>>>>>>> 014a1fd10945a19fe5b84da52a2dd6ccb772e5ba

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_seq_id")
    @SequenceGenerator(name = "user_seq_id",sequenceName = "user_seq_id",allocationSize = 1)
    private Long id;
    private String name;

    @NaturalId
    private String email;
    private String password;
    private boolean isDeleted = false ;
    private LocalDateTime deletionDate;
    private boolean isEnabled = false;


/*--------------------------------Relation------------------------------------*/
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}

