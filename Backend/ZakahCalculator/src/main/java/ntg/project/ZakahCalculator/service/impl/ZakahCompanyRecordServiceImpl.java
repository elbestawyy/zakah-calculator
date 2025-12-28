package ntg.project.ZakahCalculator.service.impl;

import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordSummaryResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
import ntg.project.ZakahCalculator.entity.util.ZakahStatus;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.mapper.ZakahCompanyRecordMapper;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.repository.ZakahCompanyRecordRepository;
import ntg.project.ZakahCalculator.service.ZakahCompanyRecordService;
import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.util.UserUtil;
import org.springframework.context.annotation.Lazy;
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

public class ZakahCompanyRecordServiceImpl implements ZakahCompanyRecordService {

    //Depencency Injection
    private final UserUtil userIDUtility;
    private final ZakahCompanyRecordRepository zakahCompanyRecordRepository;
    private final UserRepository userRepository;
    private final ZakahCompanyRecordMapper zakahCompanyRecordMapper;

    //Constants
    private static final BigDecimal ZAKAH_RATE = new BigDecimal("0.02577"); // 2.5%
    private static final BigDecimal NISAB_THRESHOLD = new BigDecimal("85"); // 85 grams of gold equivalent
    private static final long HAWL_PERIOD_DAYS = 365;

    @Override
    public ZakahCompanyRecordResponse save(ZakahCompanyRecordRequest request) {
        validateRequest(request);
        Long userId = userIDUtility.getAuthenticatedUserId();

        if (!userId.equals(request.getUserId())) {
            throw new BusinessException(UNAUTHORIZED_ZAKAH_ACCESS, request.getUserId());}

        LocalDate balanceSheetDate = request.getBalance_sheet_date();
        BigDecimal goldPrice = request.getGoldPrice();
        // Step 1: Calculate total assets
        BigDecimal totalAssets = calculateTotalAssets(
                request.getCashEquivalents(),
                request.getAccountsReceivable(),
                request.getInventory(),
                request.getInvestment()
        );

        // Step 2: Calculate total liabilities
        BigDecimal totalLiabilities = calculateTotalLiabilities(
                request.getAccountsPayable(),
                request.getShortTermLiability(),
                request.getAccruedExpenses(),
                request.getYearly_long_term_liabilities()
        );

        // Step 3: Calculate zakah pool (net wealth)
        BigDecimal zakahPool = totalAssets.subtract(totalLiabilities);
        if (zakahPool.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(NEGATIVE_ZAKAH_POOL);
        }

        // Step 4: Calculate nisab value in currency (85 grams × gold price per gram)
        BigDecimal nisabValue = NISAB_THRESHOLD.multiply(goldPrice);

        // Step 5: Determine status and calculate zakah amount
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
            // Wealth is above nisab - calculate zakah
            zakahAmount = calculateZakah(zakahPool);

            // Check if this is first calculation or if hawl period is completed
            Optional<ZakahCompanyRecord> lastRecordOpt = zakahCompanyRecordRepository
                    .findTopByUserIdOrderByBalanceSheetDateDesc(userId);

            if (lastRecordOpt.isPresent()) {
                ZakahCompanyRecord lastRecord = lastRecordOpt.get();
                long daysBetween = ChronoUnit.DAYS.between(
                        lastRecord.getBalanceSheetDate(),
                        balanceSheetDate
                );

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

        // Step 6: Get user from database
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, request.getUserId()));


        // Step 7: Convert DTO to entity using mapper
        ZakahCompanyRecord record = zakahCompanyRecordMapper.toEntity(request, user);

        // Step 8: Set calculated values
        record.setZakahAmount(zakahAmount);
        record.setStatus(status);

        // Step 9: Save to database
        ZakahCompanyRecord savedRecord = zakahCompanyRecordRepository.save(record);
        log.info("Zakah calculation completed successfully for user: {} with status: {}",
                userId, status);

/*        return buildResponse(
                request, savedRecord, totalAssets, totalLiabilities,
                zakahPool, zakahAmount, status, message, userId
        );*/

        // Step 10: Prepare response with comparison to previous records
        ZakahCompanyRecordResponse response = new ZakahCompanyRecordResponse();
        response.setId(savedRecord.getId());
        response.setStatus(status);
        response.setStatusDescription(status.getDescription());

        // Set all assets
        response.setCashEquivalents(request.getCashEquivalents());
        response.setAccountsReceivable(request.getAccountsReceivable());
        response.setInventory(request.getInventory());
        response.setInvestment(request.getInvestment());

        // Set all liabilities
        response.setAccountsPayable(request.getAccountsPayable());
        response.setShortTermLiability(request.getShortTermLiability());
        response.setAccruedExpenses(request.getAccruedExpenses());
        response.setYearlyLongTermLiabilities(request.getYearly_long_term_liabilities());

        // Set zakah information
        response.setGoldPrice(goldPrice);
        response.setUserId(userId);
        response.setTotalAssets(totalAssets);
        response.setTotalLiabilities(totalLiabilities);
        response.setCurrentZakahPool(zakahPool);
        response.setZakahAmount(zakahAmount);
        response.setBalanceSheetDate(balanceSheetDate);
        response.setMessage(message);

        // Step 11: Compare with previous record if exists
        Optional<ZakahCompanyRecord> lastRecordOpt = zakahCompanyRecordRepository
                .findTopByUserIdOrderByBalanceSheetDateDesc(userId);

        if (lastRecordOpt.isPresent()) {
            ZakahCompanyRecord lastRecord = lastRecordOpt.get();

            // Calculate difference
            BigDecimal zakahDifference = zakahAmount.subtract(lastRecord.getZakahAmount());
            response.setPreviousZakahAmount(lastRecord.getZakahAmount());
            response.setZakahDifference(zakahDifference);

            // Calculate days between records
            long daysBetween = ChronoUnit.DAYS.between(
                    lastRecord.getBalanceSheetDate(),
                    balanceSheetDate
            );
            response.setDaysSinceLastCalculation(daysBetween);

            // Check if hawl is completed
            boolean hawlCompleted = daysBetween >= HAWL_PERIOD_DAYS;
            response.setHawlCompleted(hawlCompleted);
        } else {
            // First record - no comparison
            response.setHawlCompleted(true);
            response.setDaysSinceLastCalculation(0);
        }

        return response;
    }
    // --------------------- DETAILED RESPONSE METHODS ---------------------
    @Override
    public ZakahCompanyRecordResponse findByIdAndUserId(Long id) {
        Long userId = userIDUtility.getAuthenticatedUserId();

        ZakahCompanyRecord record = zakahCompanyRecordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        return zakahCompanyRecordMapper.toDetailedResponse(record);
    }

    @Override
    public ZakahCompanyRecordResponse findLatestByUserId() {
        Long userId = userIDUtility.getAuthenticatedUserId();

        log.info("Fetching latest zakah record for user: {}", userId);

        ZakahCompanyRecord latestRecord = zakahCompanyRecordRepository
                .findTopByUserIdOrderByBalanceSheetDateDesc(userId)
                .orElseThrow(() -> new BusinessException(
                        ZAKAH_RECORD_NOT_FOUND,
                        "No zakah records found for user"
                ));

        log.info("Found latest zakah record with id: {} for user: {}", latestRecord.getId(), userId);

        return zakahCompanyRecordMapper.toDetailedResponse(latestRecord);
    }

    @Override
    public List<ZakahCompanyRecordResponse> findAllByUserId() {
        try {
            Long userId = userIDUtility.getAuthenticatedUserId();

            List<ZakahCompanyRecord> records = zakahCompanyRecordRepository.findAllByUserId(userId);

            return records.stream()
                    .map(zakahCompanyRecordMapper::toDetailedResponse)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error fetching records for user", ex);
            throw new BusinessException(ZAKAH_CALCULATION_FAILED, "Failed to retrieve zakah records");
        }
    }

    //----------------------------------------------------------------------
    @Override
    public void deleteByIdAndUserId(Long id) {
        Long userId = userIDUtility.getAuthenticatedUserId();

        ZakahCompanyRecord record = zakahCompanyRecordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        zakahCompanyRecordRepository.deleteByIdAndUserId(id, userId);

        log.info("Successfully deleted zakah record with id: {} for user: {}", id, userId);
    }
    @Override
    public Optional<ZakahCompanyRecord> findTopByUserIdOrderByBalanceSheetDateDesc(Long userId) {
        return zakahCompanyRecordRepository.findTopByUserIdOrderByBalanceSheetDateDesc(userId);
    }

    // --------------------- SUMMARY RESPONSE METHODS ---------------------
    @Override
    public ZakahCompanyRecordSummaryResponse findSummaryByIdAndUserId(Long id) {
        Long userId = userIDUtility.getAuthenticatedUserId();

        ZakahCompanyRecord record = zakahCompanyRecordRepository.findByIdAndUserId(id, userId);

        if (record == null) {
            throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);
        }

        return zakahCompanyRecordMapper.toSummaryResponse(record);
    }

    @Override
    public ZakahCompanyRecordSummaryResponse findLatestSummaryByUserId() {
        Long userId = userIDUtility.getAuthenticatedUserId();

        log.info("Fetching latest zakah record summary for user: {}", userId);

        ZakahCompanyRecord latestRecord = zakahCompanyRecordRepository
                .findTopByUserIdOrderByBalanceSheetDateDesc(userId)
                .orElseThrow(() -> new BusinessException(
                        ZAKAH_RECORD_NOT_FOUND,
                        "No zakah records found for user"
                ));

        log.info("Found latest zakah record summary with id: {} for user: {}", latestRecord.getId(), userId);

        return zakahCompanyRecordMapper.toSummaryResponse(latestRecord);
    }

    @Override
    public List<ZakahCompanyRecordSummaryResponse> findAllSummariesByUserId() {
        try {
            Long userId = userIDUtility.getAuthenticatedUserId();

            List<ZakahCompanyRecord> records = zakahCompanyRecordRepository.findAllByUserId(userId);

            return records.stream()
                    .map(zakahCompanyRecordMapper::toSummaryResponse)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error fetching record summaries for user", ex);
            throw new BusinessException(ZAKAH_CALCULATION_FAILED, "Failed to retrieve zakah record summaries");
        }
    }



    //----------------------------Calculation method logic-------------------------------------------------------------

    //Calculate total assets
    private BigDecimal calculateTotalAssets(
            BigDecimal cashEquivalents,
            BigDecimal accountsReceivable,
            BigDecimal inventory,
            BigDecimal investment) {

        BigDecimal total = BigDecimal.ZERO;

        if (cashEquivalents != null) total = total.add(cashEquivalents);
        if (accountsReceivable != null) total = total.add(accountsReceivable);
        if (inventory != null) total = total.add(inventory);
        if (investment != null) total = total.add(investment);

        return total;
    }

    //Calculate total liabilities
    private BigDecimal calculateTotalLiabilities(
            BigDecimal accountsPayable,
            BigDecimal shortTermLiability,
            BigDecimal accruedExpenses,
            BigDecimal yearlyLongTermLiabilities) {

        BigDecimal total = BigDecimal.ZERO;

        if (accountsPayable != null) total = total.add(accountsPayable);
        if (shortTermLiability != null) total = total.add(shortTermLiability);
        if (accruedExpenses != null) total = total.add(accruedExpenses);
        if (yearlyLongTermLiabilities != null) total = total.add(yearlyLongTermLiabilities);

        return total;
    }

    private BigDecimal calculateZakah(BigDecimal ZakahPool) {
        if (ZakahPool.compareTo(NISAB_THRESHOLD) < 0) {
            return BigDecimal.ZERO;
        }
        return ZakahPool.multiply(ZAKAH_RATE);
    }

    //------------------------------- VALIDATION METHODS-----------------------------------------------------------
    private void validateRequest(ZakahCompanyRecordRequest request) {
        if (request == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "Request cannot be null");
        }

        if (request.getUserId() == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "User ID is required");
        }

        if (request.getGoldPrice() == null || request.getGoldPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(GOLD_PRICE_INVALID);
        }

        if (request.getBalance_sheet_date() == null) {
            throw new BusinessException(INVALID_ZAKAH_DATA, "Balance sheet date is required");
        }

        validateNonNegative(request.getCashEquivalents(), "Cash equivalents");
        validateNonNegative(request.getAccountsReceivable(), "Accounts receivable");
        validateNonNegative(request.getInventory(), "Inventory");
        validateNonNegative(request.getInvestment(), "Investment");
        validateNonNegative(request.getAccountsPayable(), "Accounts payable");
        validateNonNegative(request.getShortTermLiability(), "Short term liability");
        validateNonNegative(request.getAccruedExpenses(), "Accrued expenses");
        validateNonNegative(request.getYearly_long_term_liabilities(), "Yearly long term liabilities");
    }

    private void validateNonNegative(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(NEGATIVE_FINANCIAL_VALUE, fieldName);
        }
    }

    private void validateBalanceSheetDate(LocalDate balanceSheetDate) {
        LocalDate today = LocalDate.now();

        if (balanceSheetDate.isAfter(today)) {
            throw new BusinessException(INVALID_BALANCE_SHEET_DATE,
                    "Balance sheet date cannot be in the future: " + balanceSheetDate);
        }
    }
}