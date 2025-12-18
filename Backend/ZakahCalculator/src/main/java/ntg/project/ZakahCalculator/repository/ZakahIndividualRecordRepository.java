package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;
import ntg.project.ZakahCalculator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZakahIndividualRecordRepository
        extends JpaRepository<ZakahIndividualRecord, Long> {

    List<ZakahIndividualRecord> findByUser(User user);
}
