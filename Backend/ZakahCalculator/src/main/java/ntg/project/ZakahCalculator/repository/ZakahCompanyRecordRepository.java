package ntg.project.ZakahCalculator.repository;

import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ZakahCompanyRecordRepository
        extends JpaRepository<ZakahCompanyRecord, Long> {

    //Get Balance sheet records by user id and year
    ZakahCompanyRecord findByIdAndUserId(Long id, Long userId);

    //Get all balance sheet records by user id
    @Query("""
    SELECT z
    FROM ZakahCompanyRecord z
    WHERE z.user.id = :userId
    ORDER BY z.balanceSheetDate DESC, z.id DESC
""")
    List<ZakahCompanyRecord> findAllByUserIdOrderByLatest(Long userId);

    //Delete balance sheet record by id and user id
    void deleteByIdAndUserId(Long id,Long userId);

    //Get the latest balance sheet record by user id
    Optional<ZakahCompanyRecord> findTopByUserIdAndStatusInOrderByBalanceSheetDateDesc(Long userId,List<ZakahStatus> statuses);
    Optional<ZakahCompanyRecord>
    findTopByUserIdAndStatusNotInOrderByBalanceSheetDateDesc(
            Long userId,
            List<ZakahStatus> excludedStatuses
    );



}
