package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZakahCompanyRecordRepository
        extends JpaRepository<ZakahCompanyRecord, Long> {

    List<ZakahCompanyRecord> findByUser(User user);
}
