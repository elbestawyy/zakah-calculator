package ntg.project.ZakahCalculator.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ntg.project.ZakahCalculator.entity.util.BaseEntity;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;

import java.math.BigDecimal;


@Entity
@Table(name = "zakah_records")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "record_type", discriminatorType = DiscriminatorType.STRING)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZakahRecord extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "zakahRecord_seq_id")
        @SequenceGenerator(name = "zakahRecord_seq_id",sequenceName = "zakahRecord_seq_id",allocationSize = 1)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false, precision = 15, scale = 2)
        private BigDecimal zakahAmount;

        @Column(name = "gold_price", nullable = false, precision = 10, scale = 2)
        private BigDecimal goldPrice;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private ZakahStatus status;
    }

