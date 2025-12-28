package ntg.project.ZakahCalculator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.ZakahIndividualRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahIndividualRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.ZakahIndividualRecord;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.mapper.ZakahIndividualRecordMapper;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.repository.ZakahIndividualRecordRepository;
import ntg.project.ZakahCalculator.service.ZakahIndividualRecordService;
import ntg.project.ZakahCalculator.util.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ntg.project.ZakahCalculator.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ZakahIndividualRecordServiceImpl implements ZakahIndividualRecordService {

    private final UserUtil userIDUtility;
    private final ZakahIndividualRecordRepository zakahIndividualRecordRepository;
    private final UserRepository userRepository;
    private final ZakahIndividualRecordMapper zakahIndividualRecordMapper;

    // Constants
    private static final BigDecimal ZAKAH_RATE = new BigDecimal("0.02577");
    private static final BigDecimal NISAB_THRESHOLD = new BigDecimal("85");
    private static final long HAWL_PERIOD_DAYS = 365;

    @Override
    public ZakahIndividualRecordResponse save(ZakahIndividualRecordRequest request) {
        try {
            log.info("Starting individual zakah calculation for user: {}", request.getUserId());

            validateRequest(request);

            Long userId = userIDUtility.getAuthenticatedUserId();

            if (!userId.equals(request.getUserId())) {
                throw new BusinessException(UNAUTHORIZED_ZAKAH_ACCESS, request.getUserId());
            }

            LocalDate calculationDate = request.getCalculationDate();
            BigDecimal goldPrice = request.getGoldPrice();

            validateCalculationDate(calculationDate);

            // Step 1: Calculate total assets (no liabilities for individuals)
            BigDecimal totalAssets = calculateTotalAssets(
                    request.getCash(),
                    request.getGold(),
                    request.getSilver(),
                    request.getStocks(),
                    request.getBonds()
            );
            BigDecimal zakahPool = totalAssets;

            // Step 3: Calculate nisab
            BigDecimal nisabValue = NISAB_THRESHOLD.multiply(goldPrice);

            // Step 4: Determine status and calculate zakah amount
            ZakahStatus status;
            BigDecimal zakahAmount;
            String message;

            if (zakahPool.compareTo(nisabValue) < 0) {
                // Wealth is below nisab - no zakah required
                status = ZakahStatus.BELOW_NISAB;
                zakahAmount = BigDecimal.ZERO;
                message = String.format(
                        "Your wealth (%.2f) is below the nisab threshold (%.2f). No zakah is required.",
                        zakahPool, nisabValue
                );
            } else {
                // Amount is above nisab - calculate zakah
                zakahAmount = calculateZakahAmount(zakahPool);

                // Check if this is first calculation or if hawl period is completed
                Optional<ZakahIndividualRecord> lastRecordOpt = zakahIndividualRecordRepository
                        .findTopByUserIdOrderByCreatedAtDesc(userId);

                if (lastRecordOpt.isPresent()) {
                    ZakahIndividualRecord lastRecord = lastRecordOpt.get();
                    long daysBetween = ChronoUnit.DAYS.between(
                            lastRecord.getCalculationDate(),
                            calculationDate
                    );

                    if (daysBetween < 0) {
                        throw new BusinessException(
                                BALANCE_SHEET_DATE_BEFORE_LAST_RECORD,
                                lastRecord.getCalculationDate()
                        );
                    }

                    if (daysBetween < HAWL_PERIOD_DAYS) {
                        // Hawl period not completed yet
                        status = ZakahStatus.HAWL_NOT_COMPLETED;
                        long daysRemaining = HAWL_PERIOD_DAYS - daysBetween;
                        message = String.format(
                                "Warning: Hawl period not yet completed. Days remaining: %d. " +
                                        "Estimated zakah when due: %.2f",
                                daysRemaining, zakahAmount
                        );
                    } else {
                        // Hawl period completed - zakah is due
                        status = ZakahStatus.ZAKAH_DUE;
                        message = String.format("Hawl period completed. Zakah is due: %.2f", zakahAmount);
                    }
                } else {
                    // First calculation for this user
                    status = ZakahStatus.ZAKAH_DUE;
                    message = "First zakah calculation for this user.";
                }
            }

            // Step 5: Get user from database
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, request.getUserId()));

            // Step 6: Convert DTO to entity using mapper
            ZakahIndividualRecord record = zakahIndividualRecordMapper.toEntity(request, user);

            // Step 7: Set calculated values
            record.setZakahAmount(zakahAmount);
            record.setStatus(status);

            // Step 8: Save to database
            ZakahIndividualRecord savedRecord = zakahIndividualRecordRepository.save(record);

            log.info("Individual zakah calculation completed successfully for user: {} with status: {}",
                    userId, status);

            // Step 9: Prepare response
            return buildResponse(
                    request, savedRecord, totalAssets, zakahAmount, status, message, userId
            );

        } catch (BusinessException ex) {
            log.error("Business exception during individual zakah calculation: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during individual zakah calculation", ex);
            throw new BusinessException(ZAKAH_CALCULATION_FAILED, ex.getMessage());
        }
    }

    /**
     * Update existing individual zakah record with new data.
     */
/*    @Override
    public ZakahIndividualRecordResponse update(Long id, ZakahIndividualRecordRequest request) {
        try {
            log.info("Starting update for individual zakah record id: {}", id);

            Long userId = userIDUtility.getAuthenticatedUserId();

            // Step 1: Verify record exists and user owns it
            ZakahIndividualRecord existingRecord = zakahIndividualRecordRepository.findByIdAndUserId(id, userId);

            if (existingRecord == null) {
                throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
            }

            // Step 2: Validate new request data
            validateRequest(request);

            if (!userId.equals(request.getUserId())) {
                throw new BusinessException(UNAUTHORIZED_ZAKAH_ACCESS, id);
            }

            LocalDate calculationDate = request.getCalculationDate();
            BigDecimal goldPrice = request.getGoldPrice();

            validateCalculationDate(calculationDate);

            // Step 3: Calculate new totals
            BigDecimal totalAssets = calculateTotalAssets(
                    request.getCash(),
                    request.getGold(),
                    request.getSilver(),
                    request.getStocks(),
                    request.getBonds()
            );

            BigDecimal zakahPool = totalAssets;
            BigDecimal nisabValue = NISAB_THRESHOLD.multiply(goldPrice);

            // Step 4: Determine new status
            ZakahStatus status;
            BigDecimal zakahAmount;
            String message;

            if (zakahPool.compareTo(nisabValue) < 0) {
                status = ZakahStatus.BELOW_NISAB;
                zakahAmount = BigDecimal.ZERO;
                message = String.format(
                        "Your wealth (%.2f) is below the nisab threshold (%.2f). No zakah is required.",
                        zakahPool, nisabValue
                );
            } else {
                zakahAmount = calculateZakahAmount(zakahPool);

                // Find previous record (excluding current one being updated)
                Optional<ZakahIndividualRecord> previousRecordOpt = zakahIndividualRecordRepository
                        .findAllByUserId(userId)
                        .stream()
                        .filter(record -> !record.getId().equals(id))
                        .filter(record -> record.getCreatedAt().toLocalDate().isBefore(calculationDate))
                        .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()));

                if (previousRecordOpt.isPresent()) {
                    ZakahIndividualRecord previousRecord = previousRecordOpt.get();
                    long daysBetween = ChronoUnit.DAYS.between(
                            previousRecord.getCreatedAt().toLocalDate(),
                            calculationDate
                    );

                    if (daysBetween < 0) {
                        throw new BusinessException(
                                BALANCE_SHEET_DATE_BEFORE_LAST_RECORD,
                                previousRecord.getCreatedAt().toLocalDate()
                        );
                    }

                    if (daysBetween < HAWL_PERIOD_DAYS) {
                        status = ZakahStatus.HAWL_NOT_COMPLETED;
                        long daysRemaining = HAWL_PERIOD_DAYS - daysBetween;
                        message = String.format(
                                "Warning: Hawl period (lunar year) not yet completed. Days remaining: %d. " +
                                        "Estimated zakah when due: %.2f",
                                daysRemaining, zakahAmount
                        );
                    } else {
                        status = ZakahStatus.ZAKAH_DUE;
                        message = String.format("Hawl period completed. Zakah is due: %.2f", zakahAmount);
                    }
                } else {
                    status = ZakahStatus.ZAKAH_DUE;
                    message = "First zakah calculation for this user.";
                }
            }

            // Step 5: Update existing record with new values
            existingRecord.setCash(request.getCash() != null ? request.getCash() : BigDecimal.ZERO);
            existingRecord.setGold(request.getGold() != null ? request.getGold() : BigDecimal.ZERO);
            existingRecord.setSilver(request.getSilver() != null ? request.getSilver() : BigDecimal.ZERO);
            existingRecord.setStocks(request.getStocks() != null ? request.getStocks() : BigDecimal.ZERO);
            existingRecord.setBonds(request.getBonds() != null ? request.getBonds() : BigDecimal.ZERO);
            existingRecord.setGoldPrice(goldPrice);
            existingRecord.setZakahAmount(zakahAmount);
            existingRecord.setStatus(status);

            // Step 6: Save updated record
            ZakahIndividualRecord updatedRecord = zakahIndividualRecordRepository.save(existingRecord);

            log.info("Individual zakah record updated successfully with id: {} and status: {}", id, status);

            // Step 7: Prepare response
            return buildResponse(
                    request, updatedRecord, totalAssets, zakahAmount, status, message, userId
            );

        } catch (BusinessException ex) {
            log.error("Business exception during individual zakah record update: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during individual zakah record update", ex);
            throw new BusinessException(ZAKAH_CALCULATION_FAILED, "Failed to update individual zakah record: " + ex.getMessage());
        }
    }
*/
    @Override
    public ZakahIndividualRecordResponse findByIdAndUserId(Long id) {
        Long userId = userIDUtility.getAuthenticatedUserId();

        ZakahIndividualRecord record = zakahIndividualRecordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        return zakahIndividualRecordMapper.toDetailedResponse(record);
    }

    @Override
    public List<ZakahIndividualRecordResponse> findAllByUserId() {
        try {
            Long userId = userIDUtility.getAuthenticatedUserId();

            List<ZakahIndividualRecord> records = zakahIndividualRecordRepository.findAllByUserId(userId);

            return records.stream()
                    .map(zakahIndividualRecordMapper::toDetailedResponse)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error fetching individual records for user", ex);
            throw new BusinessException(ZAKAH_CALCULATION_FAILED, "Failed to retrieve individual zakah records");
        }
    }

    @Override
    public ZakahIndividualRecordSummaryResponse findSummaryByIdAndUserId(Long id) {
        Long userId = userIDUtility.getAuthenticatedUserId();

        ZakahIndividualRecord record = zakahIndividualRecordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        return zakahIndividualRecordMapper.toSummaryResponse(record);
    }

    @Override
    public List<ZakahIndividualRecordSummaryResponse> findAllSummariesByUserId() {
        try {
            Long userId = userIDUtility.getAuthenticatedUserId();

            List<ZakahIndividualRecord> records = zakahIndividualRecordRepository.findAllByUserId(userId);

            return records.stream()
                    .map(zakahIndividualRecordMapper::toSummaryResponse)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error fetching individual record summaries for user", ex);
            throw new BusinessException(ZAKAH_CALCULATION_FAILED, "Failed to retrieve individual zakah record summaries");
        }
    }

    @Override
    public ZakahIndividualRecordResponse findLatestByUserId() {
        Long userId = userIDUtility.getAuthenticatedUserId();

        log.info("Fetching latest individual zakah record for user: {}", userId);

        ZakahIndividualRecord latestRecord = zakahIndividualRecordRepository
                .findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException(
                        ZAKAH_RECORD_NOT_FOUND,
                        "No individual zakah records found for user"
                ));

        log.info("Found latest individual zakah record with id: {} for user: {}", latestRecord.getId(), userId);

        return zakahIndividualRecordMapper.toDetailedResponse(latestRecord);
    }

    @Override
    public ZakahIndividualRecordSummaryResponse findLatestSummaryByUserId() {
        Long userId = userIDUtility.getAuthenticatedUserId();

        log.info("Fetching latest individual zakah record summary for user: {}", userId);

        ZakahIndividualRecord latestRecord = zakahIndividualRecordRepository
                .findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException(
                        ZAKAH_RECORD_NOT_FOUND,
                        "No individual zakah records found for user"
                ));

        log.info("Found latest individual zakah record summary with id: {} for user: {}", latestRecord.getId(), userId);

        return zakahIndividualRecordMapper.toSummaryResponse(latestRecord);
    }

    @Override
    public void deleteByIdAndUserId(Long id) {
        Long userId = userIDUtility.getAuthenticatedUserId();

        ZakahIndividualRecord record = zakahIndividualRecordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        zakahIndividualRecordRepository.deleteByIdAndUserId(id, userId);

        log.info("Successfully deleted individual zakah record with id: {} for user: {}", id, userId);
    }

    @Override
    public Optional<ZakahIndividualRecord> findTopByUserIdOrderByCreatedAtDesc(Long userId) {
        return zakahIndividualRecordRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }

    // ==================== VALIDATION METHODS ====================

    private void validateRequest(ZakahIndividualRecordRequest request) {
        if (request == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "Request cannot be null");
        }

        if (request.getUserId() == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "User ID is required");
        }

        if (request.getGoldPrice() == null || request.getGoldPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(GOLD_PRICE_INVALID);
        }

        if (request.getCalculationDate() == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "Calculation date is required");
        }

        // Validate that financial values are not negative
        validateNonNegative(request.getCash(), "Cash");
        validateNonNegative(request.getGold(), "Gold");
        validateNonNegative(request.getSilver(), "Silver");
        validateNonNegative(request.getStocks(), "Stocks");
        validateNonNegative(request.getBonds(), "Bonds");
    }

    private void validateNonNegative(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(NEGATIVE_FINANCIAL_VALUE, fieldName);
        }
    }

    private void validateCalculationDate(LocalDate calculationDate) {
        LocalDate today = LocalDate.now();

        // Don't allow future dates
        if (calculationDate.isAfter(today)) {
            throw new BusinessException(INVALID_BALANCE_SHEET_DATE,
                    "Calculation date cannot be in the future: " + calculationDate);
        }
    }
    // ==================== CALCULATION METHODS ====================

    // Calculate total assets for individual

    private BigDecimal calculateTotalAssets(
            BigDecimal cash,
            BigDecimal gold,
            BigDecimal silver,
            BigDecimal stocks,
            BigDecimal bonds) {

        BigDecimal total = BigDecimal.ZERO;
        if (cash != null) total = total.add(cash);
        if (gold != null) total = total.add(gold);
        if (silver != null) total = total.add(silver);
        if (stocks != null) total = total.add(stocks);
        if (bonds != null) total = total.add(bonds);
        return total;
    }

    // Calculate zakah amount (2.5% of zakah pool)

    private BigDecimal calculateZakahAmount(BigDecimal zakahPool) {
        return zakahPool.multiply(ZAKAH_RATE);
    }

    // Build complete response with all details
    private ZakahIndividualRecordResponse buildResponse(
            ZakahIndividualRecordRequest request,
            ZakahIndividualRecord savedRecord,
            BigDecimal totalAssets,
            BigDecimal zakahAmount,
            ZakahStatus status,
            String message,
            Long userId) {

        ZakahIndividualRecordResponse response = new ZakahIndividualRecordResponse();
        response.setId(savedRecord.getId());
        response.setStatus(status);
        response.setStatusDescription(status.getDescription());

        // Set all assets
        response.setCash(request.getCash());
        response.setGold(request.getGold());
        response.setSilver(request.getSilver());
        response.setStocks(request.getStocks());
        response.setBonds(request.getBonds());

        // Set zakah information
        response.setGoldPrice(request.getGoldPrice());
        response.setUserId(userId);
        response.setTotalAssets(totalAssets);
        response.setZakahPool(totalAssets); // For individuals, zakah pool = total assets
        response.setZakahAmount(zakahAmount);
        response.setCalculationDate(savedRecord.getCalculationDate());
        response.setMessage(message);

        // Compare with previous record if exists
        Optional<ZakahIndividualRecord> lastRecordOpt = zakahIndividualRecordRepository
                .findTopByUserIdOrderByCreatedAtDesc(userId);

        if (lastRecordOpt.isPresent() && !lastRecordOpt.get().getId().equals(savedRecord.getId())) {
            ZakahIndividualRecord lastRecord = lastRecordOpt.get();

            BigDecimal zakahDifference = zakahAmount.subtract(lastRecord.getZakahAmount());
            response.setPreviousZakahAmount(lastRecord.getZakahAmount());
            response.setZakahDifference(zakahDifference);

            long daysBetween = ChronoUnit.DAYS.between(
                    lastRecord.getCreatedAt().toLocalDate(),
                    request.getCalculationDate()
            );
            response.setDaysSinceLastCalculation(daysBetween);

            boolean hawlCompleted = daysBetween >= HAWL_PERIOD_DAYS;
            response.setHawlCompleted(hawlCompleted);
        } else {
            response.setHawlCompleted(true);
            response.setDaysSinceLastCalculation(0);
        }

        return response;
    }
}