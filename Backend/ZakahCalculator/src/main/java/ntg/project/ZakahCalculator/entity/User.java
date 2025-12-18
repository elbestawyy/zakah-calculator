package ntg.project.ZakahCalculator.entity;

import jakarta.persistence.*;
import lombok.*;
import ntg.project.ZakahCalculator.entity.util.BaseEntity;
import org.hibernate.annotations.NaturalId;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User extends BaseEntity {

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



/*--------------------------------Relation------------------------------------*/
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

}

