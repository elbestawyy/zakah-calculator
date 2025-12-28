package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.request.ZakahIndividualRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;

import java.util.List;
import java.util.Optional;

public interface ZakahIndividualRecordService {

    // Main method: Calculate zakah, determine status, compare, and save
    ZakahIndividualRecordResponse save(ZakahIndividualRecordRequest request);

    // Update method: Recalculate zakah and update existing record
    ZakahIndividualRecordResponse update(Long id, ZakahIndividualRecordRequest request);



    // Detailed responses (all fields)
    ZakahIndividualRecordResponse findByIdAndUserId(Long id);
    List<ZakahIndividualRecordResponse> findAllByUserId();

    // Summary responses (only totals, zakah amount, status)
    ZakahIndividualRecordSummaryResponse findSummaryByIdAndUserId(Long id);
    List<ZakahIndividualRecordSummaryResponse> findAllSummariesByUserId();

    // Get latest zakah record
    ZakahIndividualRecordResponse findLatestByUserId();
    ZakahIndividualRecordSummaryResponse findLatestSummaryByUserId();

    void deleteByIdAndUserId(Long id);

    Optional<ZakahIndividualRecord> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}