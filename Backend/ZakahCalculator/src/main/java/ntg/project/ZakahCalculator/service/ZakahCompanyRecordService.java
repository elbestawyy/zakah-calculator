package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;

import java.util.List;
import java.util.Optional;

public interface ZakahCompanyRecordService {
    // Main method: Calculate zakah, determine status, compare, and save
    ZakahCompanyRecordResponse save(ZakahCompanyRecordRequest request);
    //Detailed responses
    List<ZakahCompanyRecordResponse> findAllByUserId();
    ZakahCompanyRecordResponse findByIdAndUserId(Long id);


    // Summary responses
    ZakahCompanyRecordSummaryResponse findSummaryByIdAndUserId(Long id);
    List<ZakahCompanyRecordSummaryResponse> findAllSummariesByUserId();

    //latest zakah record
    ZakahCompanyRecordResponse findLatestByUserId();
    ZakahCompanyRecordSummaryResponse findLatestSummaryByUserId();

    Optional<ZakahCompanyRecord> findTopByUserIdOrderByBalanceSheetDateDesc(Long userId);
    void deleteByIdAndUserId(Long id);
}
