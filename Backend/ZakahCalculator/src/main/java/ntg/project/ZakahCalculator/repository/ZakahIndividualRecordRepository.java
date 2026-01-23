package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ZakahIndividualRecordRepository extends JpaRepository<ZakahIndividualRecord, Long> {

    // Get individual zakah record by id and user id
    ZakahIndividualRecord findByIdAndUserId(Long id, Long userId);

    List<ZakahIndividualRecord> findAllByUser_IdOrderByCalculationDateDescCreatedAtDesc(Long userId);



    // Delete individual zakah record by id and user id
    void deleteByIdAndUserId(Long id, Long userId);

    // Get the latest individual zakah record by user id
    Optional<ZakahIndividualRecord> findTopByUserIdOrderByCalculationDateDesc(Long userId);

    Optional<ZakahIndividualRecord>
    findTopByUserIdAndStatusNotInOrderByCalculationDateDesc(
            Long userId,
            List<ZakahStatus> excludedStatuses
    );


}