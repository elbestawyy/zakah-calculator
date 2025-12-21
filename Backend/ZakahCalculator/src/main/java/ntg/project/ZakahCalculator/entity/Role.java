package ntg.project.ZakahCalculator.entity;

import jakarta.persistence.*;
import lombok.*;
import ntg.project.ZakahCalculator.entity.util.UserType;

import java.util.List;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @SequenceGenerator(name = "role_seq_id",sequenceName = "role_seq_id",allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserType name; // INDIVIDUAL, COMPANY, ADMIN

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    // getters & setters
}
