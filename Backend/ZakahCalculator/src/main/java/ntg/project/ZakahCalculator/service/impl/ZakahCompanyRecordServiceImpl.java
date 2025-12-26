package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.dto.request.ZakahCompanyRecordRequest;
import ntg.project.ZakahCalculator.dto.response.ZakahCompanyRecordResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.ZakahCompanyRecord;
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

@Service
@RequiredArgsConstructor
@Transactional
public class ZakahCompanyRecordServiceImpl implements ZakahCompanyRecordService {

    //Depencency Injection
    private final UserUtil userIDUtility;
    private final ZakahCompanyRecordRepository zakahCompanyRecordRepository;
    private final UserRepository userRepository;
    private final ZakahCompanyRecordMapper zakahCompanyRecordMapper;

    //Constants
    private static final BigDecimal ZAKAH_RATE = new BigDecimal("0.025"); // 2.5%
    private static final BigDecimal NISAB_THRESHOLD = new BigDecimal("85"); // 85 grams of gold equivalent
    private static final long HAWL_PERIOD_DAYS = 365;

    @Override
    public ZakahCompanyRecord save(ZakahCompanyRecord record) {
        return zakahCompanyRecordRepository.save(record);
    }

    @Override
    public ZakahCompanyRecord update(ZakahCompanyRecord record, Long id) {
        return zakahCompanyRecordRepository.save(record);
    }

    @Override
    public ZakahCompanyRecord findByIdAndUserId(Long id) {
        return zakahCompanyRecordRepository.findByIdAndUserId(
                id,
                userIDUtility.getAuthenticatedUserId()
        );
    }

    @Override
    public List<ZakahCompanyRecord> findAllByUserId() {
        return zakahCompanyRecordRepository.findAllByUserId(
                userIDUtility.getAuthenticatedUserId()
        );
    }

    @Override
    public void deleteByIdAndUserId(Long id) {
        zakahCompanyRecordRepository.deleteByIdAndUserId(
                id,
                userIDUtility.getAuthenticatedUserId()
        );
    }

    @Override
    public Optional<ZakahCompanyRecord> findTopByUserIdOrderByBalanceSheetDateDesc(Long userId) {
        return zakahCompanyRecordRepository.findTopByUserIdOrderByBalanceSheetDateDesc(userId);
    }

    //----------------------------Calculation method logic-------------------------------------------------------------
    public ZakahCompanyRecordResponse calculateZakah(ZakahCompanyRecordRequest requestDTO) {
        Long userId = userIDUtility.getAuthenticatedUserId();
        LocalDate balanceSheetDate = requestDTO.getBalance_sheet_date();
        ZakahCompanyRecordResponse response = new ZakahCompanyRecordResponse();

        BigDecimal totalAssets = calculateTotalAssets(
                requestDTO.getCashEquivalents(),
                requestDTO.getAccountsReceivable(),
                requestDTO.getInventory(),
                requestDTO.getInvestment()
        );

        BigDecimal totalLiabilities = calculateTotalLiabilities(
                requestDTO.getAccountsPayable(),
                requestDTO.getShortTermLiability(),
                requestDTO.getAccruedExpenses(),
                requestDTO.getYearly_long_term_liabilities()
        );

        BigDecimal zakahPool = totalAssets.subtract(totalLiabilities);

        BigDecimal zakahAmount = calculateZakah(zakahPool);

        Optional<ZakahCompanyRecord> lastRecordOpt = zakahCompanyRecordRepository
                .findTopByUserIdOrderByBalanceSheetDateDesc(userId);

        //Set response
        response.setTotalAssets(totalAssets);
        response.setTotalLiabilities(totalLiabilities);
        response.setCurrentZakahPool(zakahPool);
        response.setZakahAmount(zakahAmount);
        response.setBalanceSheetDate(balanceSheetDate);


        if (lastRecordOpt.isPresent()) {
            ZakahCompanyRecord lastRecord = lastRecordOpt.get();


            BigDecimal zakahDifference = zakahAmount.subtract(lastRecord.getZakahAmount());
            response.setPreviousZakahAmount(lastRecord.getZakahAmount());
            response.setZakahDifference(zakahDifference);


            long daysBetween = ChronoUnit.DAYS.between(
                    lastRecord.getBalanceSheetDate(),
                    balanceSheetDate
            );

            boolean hawlCompleted = daysBetween >= HAWL_PERIOD_DAYS;
            response.setHawlCompleted(hawlCompleted);
            response.setDaysSinceLastCalculation(daysBetween);

            if (!hawlCompleted) {
                response.setMessage("Warning: Hawl period (1.3 years) not yet completed. " +
                        "Days remaining: " + (HAWL_PERIOD_DAYS - daysBetween));
            } else {
                response.setMessage("Hawl period completed. Zakah is due.");
            }


        } else {
            response.setMessage("First zakah calculation for this user.");
            response.setHawlCompleted(true);
        }

        saveZakahRecord(requestDTO, zakahAmount);
        return response;
    }

    //----------------------------Calculation methods-------------------------------------------------------------

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

    private void saveZakahRecord(
            ZakahCompanyRecordRequest requestDTO,
            BigDecimal zakahAmount) {

        // Get user from database
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + requestDTO.getUserId()));

        // Use mapper to convert DTO to entity
        ZakahCompanyRecord record = zakahCompanyRecordMapper.toEntity(requestDTO, user);

        // Set calculated values (done in service layer)
        record.setZakahAmount(zakahAmount);

        // Save to database
        zakahCompanyRecordRepository.save(record);
    }

}