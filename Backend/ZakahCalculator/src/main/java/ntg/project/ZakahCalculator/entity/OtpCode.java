package ntg.project.ZakahCalculator.entity;

import jakarta.persistence.*;
import lombok.*;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "otpCode_seq_id")
    @SequenceGenerator(name = "otpCode_seq_id",sequenceName = "otpCode_seq_id",allocationSize = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    private OtpType type;

    private boolean used = false;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusMinutes(5);
        }
        used = false;
    }

    @PreUpdate
    protected void onUpdate() {
        createdAt = LocalDateTime.now();
        used = false;
        expiresAt = LocalDateTime.now().plusMinutes(5);
    }

}

