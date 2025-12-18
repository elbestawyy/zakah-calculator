package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.OtpCode;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import ntg.project.ZakahCalculator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findByUserAndTypeAndUsedFalse(User user, OtpType type);

    Optional<OtpCode> findByCodeAndTypeAndUsedFalse(String code, OtpType type);
}
