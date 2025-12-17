package ntg.project.ZakahCalculator.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "zakah_records")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "record_type", discriminatorType = DiscriminatorType.STRING)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZakahRecord {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false, precision = 15, scale = 2)
        private BigDecimal totalZakah;

        @Column(name = "gold_price", nullable = false, precision = 10, scale = 2)
        private BigDecimal goldPrice;

        @Column(name = "created_at", nullable = false)
        private LocalDate createdAt;

        @PrePersist
        protected void onCreate() {
            if (createdAt == null) {
                createdAt = LocalDate.now();
            }
        }
    }

