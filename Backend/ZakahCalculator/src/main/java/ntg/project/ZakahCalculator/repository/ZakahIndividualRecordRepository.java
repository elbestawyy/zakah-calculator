package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZakahIndividualRecordRepository extends JpaRepository<ZakahIndividualRecord, Long> {

    // Get individual zakah record by id and user id
    ZakahIndividualRecord findByIdAndUserId(Long id, Long userId);

    // Get all individual zakah records by user id
    List<ZakahIndividualRecord> findAllByUserId(Long userId);

    // Delete individual zakah record by id and user id
    void deleteByIdAndUserId(Long id, Long userId);

    // Get the latest individual zakah record by user id
    Optional<ZakahIndividualRecord> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}