package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.exception.BusinessException;

import java.util.List;
import java.util.Optional;

import static ntg.project.ZakahCalculator.exception.ErrorCode.ZAKAH_RECORD_NOT_FOUND;

public interface ZakahCompanyRecordService {
    ZakahCompanyRecordSummaryResponse save(ZakahCompanyRecordRequest request);

    ZakahCompanyRecordResponse findById(Long id);

    List<ZakahCompanyRecordSummaryResponse> findAllSummariesByUserId();

    void deleteByIdAndUserId(Long id);
}
