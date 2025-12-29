package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.dto.request.ZakahIndividualRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordSummaryResponse;

import java.util.List;

public interface ZakahIndividualRecordService {

    ZakahIndividualRecordSummaryResponse save(ZakahIndividualRecordRequest request);

    ZakahIndividualRecordResponse findByIdAndUserId(Long id);

    List<ZakahIndividualRecordSummaryResponse> findAllSummariesByUserId();

    void deleteByIdAndUserId(Long id);
}
