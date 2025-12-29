package ntg.project.ZakahCalculator.service.impl;

import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.mapper.ZakahCompanyRecordMapper;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.repository.ZakahCompanyRecordRepository;
import ntg.project.ZakahCalculator.service.ZakahCompanyRecordService;
import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.util.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ntg.project.ZakahCalculator.exception.ErrorCode.*;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ZakahCompanyRecordServiceImpl implements ZakahCompanyRecordService {

    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final ZakahCompanyRecordRepository recordRepository;
    private final ZakahCompanyRecordMapper mapper;

    private static final BigDecimal ZAKAH_RATE = new BigDecimal("0.025");
    private static final BigDecimal NISAB_GRAMS = new BigDecimal("85");
    private static final long HAWL_DAYS = 365;

    // ================= SAVE =================
    @Override
    public ZakahCompanyRecordSummaryResponse save(ZakahCompanyRecordRequest request) {

        validateRequest(request);

        Long userId = userUtil.getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, userId));

        // ===== Calculations =====
        BigDecimal totalAssets = calculateTotalAssets(request);
        BigDecimal totalLiabilities = calculateTotalLiabilities(request);
        BigDecimal zakahPool = totalAssets.subtract(totalLiabilities);

        if (zakahPool.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(NEGATIVE_ZAKAH_POOL);
        }

        BigDecimal nisabAmount = request.getGoldPrice().multiply(NISAB_GRAMS);

        ZakahStatus status = determineStatus(
                userId,
                request.getBalance_sheet_date(),
                zakahPool,
                nisabAmount
        );

        BigDecimal zakahAmount = isZakahDue(status)
                ? zakahPool.multiply(ZAKAH_RATE)
                : BigDecimal.ZERO;

        // ===== Persist =====
        ZakahCompanyRecord record = mapper.toEntity(request, user);
        record.setStatus(status);
        record.setZakahAmount(zakahAmount);

        recordRepository.save(record);

        log.info("Zakah record saved for user {} with status {}", userId, status);

        return mapper.toSummaryResponse(record);
    }

    // ================= FIND BY ID =================
    @Override
    public ZakahCompanyRecordResponse findById(Long id) {

        Long userId = userUtil.getAuthenticatedUserId();

        ZakahCompanyRecord record =
                recordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        return mapper.toDetailedResponse(record);
    }

    // ================= FIND ALL =================
    @Override
    public List<ZakahCompanyRecordSummaryResponse> findAllSummariesByUserId() {

        Long userId = userUtil.getAuthenticatedUserId();

        return recordRepository.findAllByUserIdOrderByBalanceSheetDateDesc(userId)
                .stream()
                .map(mapper::toSummaryResponse)
                .toList();
    }

    // ================= DELETE BY ID =================
    @Override
    public void deleteByIdAndUserId(Long id) {
        Long userId = userUtil.getAuthenticatedUserId();

        ZakahCompanyRecord record = recordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        recordRepository.deleteByIdAndUserId(id, userId);

        log.info("Successfully deleted zakah record with id: {} for user: {}", id, userId);
    }

    // ================= BUSINESS RULES =================
    private ZakahStatus determineStatus(
            Long userId,
            LocalDate currentDate,
            BigDecimal zakahPool,
            BigDecimal nisabAmount) {

        if (zakahPool.compareTo(nisabAmount) < 0) {
            return ZakahStatus.BELOW_NISAB;
        }

        return recordRepository
                .findTopByUserIdOrderByBalanceSheetDateDesc(userId)
                .map(last -> {
                    long days = ChronoUnit.DAYS.between(
                            last.getBalanceSheetDate(),
                            currentDate
                    );
                    return days >= HAWL_DAYS
                            ? ZakahStatus.LAST_RECORD_DUE_AND_NEW_HAWL_BEGIN
                            : ZakahStatus.HAWL_NOT_COMPLETED;
                })
                .orElse(ZakahStatus.ELIGABLE_FOR_ZAKAH);
    }

    private boolean isZakahDue(ZakahStatus status) {
        return status == ZakahStatus.ELIGABLE_FOR_ZAKAH
                || status == ZakahStatus.LAST_RECORD_DUE_AND_NEW_HAWL_BEGIN;
    }

    // ================= CALCULATIONS =================
    private BigDecimal calculateTotalAssets(ZakahCompanyRecordRequest r) {
        return zero(r.getCashEquivalents())
                .add(zero(r.getAccountsReceivable()))
                .add(zero(r.getInventory()))
                .add(zero(r.getInvestment()));
    }

    private BigDecimal calculateTotalLiabilities(ZakahCompanyRecordRequest r) {
        return zero(r.getAccountsPayable())
                .add(zero(r.getShortTermLiability()))
                .add(zero(r.getAccruedExpenses()))
                .add(zero(r.getYearly_long_term_liabilities()));
    }

    private BigDecimal zero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // ================= VALIDATION =================
    private void validateRequest(ZakahCompanyRecordRequest request) {
        if (request == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "Request is null");
        }
        if (request.getGoldPrice() == null || request.getGoldPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(GOLD_PRICE_INVALID);
        }
        if (request.getBalance_sheet_date() == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "Balance sheet date is required");
        }
    }
}
