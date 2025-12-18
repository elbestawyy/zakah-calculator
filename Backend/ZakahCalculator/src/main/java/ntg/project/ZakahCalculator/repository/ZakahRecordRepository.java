package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.ZakahRecord;
import ntg.project.ZakahCalculator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZakahRecordRepository extends JpaRepository<ZakahRecord, Long> {

    List<ZakahRecord> findByUser(User user);
}
