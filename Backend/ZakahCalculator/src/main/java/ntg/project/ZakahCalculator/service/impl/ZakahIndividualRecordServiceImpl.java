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

    private final UserUtil userUtil;
    private final ZakahIndividualRecordRepository repository;
    private final UserRepository userRepository;
    private final ZakahIndividualRecordMapper mapper;

    private static final BigDecimal ZAKAH_RATE = new BigDecimal("0.02577");
    private static final BigDecimal NISAB_GOLD_GRAMS = new BigDecimal("85");
    private static final long HAWL_PERIOD_DAYS = 365;

    // =================== SAVE / CALCULATE ===================
    @Override
    public ZakahIndividualRecordSummaryResponse save(ZakahIndividualRecordRequest request) {
        log.info("Starting individual zakah calculation");

        validateRequest(request);

        Long userId = userUtil.getAuthenticatedUserId();
        LocalDate calculationDate = request.getCalculationDate();
        BigDecimal goldPrice = request.getGoldPrice();
        validateCalculationDate(calculationDate);

        BigDecimal totalAssets = calculateTotalAssets(
                request.getCash(), request.getGold(), request.getSilver(),
                request.getStocks(), request.getBonds()
        );

        BigDecimal nisabValue = NISAB_GOLD_GRAMS.multiply(goldPrice);

        ZakahStatus status;
        BigDecimal zakahAmount;

        if (totalAssets.compareTo(nisabValue) < 0) {
            status = ZakahStatus.BELOW_NISAB;
            zakahAmount = BigDecimal.ZERO;
        } else {
            zakahAmount = totalAssets.multiply(ZAKAH_RATE);
            Optional<ZakahIndividualRecord> lastRecordOpt =
                    repository.findTopByUserIdOrderByCreatedAtDesc(userId);

            if (lastRecordOpt.isPresent()) {
                ZakahIndividualRecord lastRecord = lastRecordOpt.get();
                long daysBetween = ChronoUnit.DAYS.between(lastRecord.getCalculationDate(), calculationDate);

                if (daysBetween < 0) {
                    throw new BusinessException(BALANCE_SHEET_DATE_BEFORE_LAST_RECORD, lastRecord.getCalculationDate());
                }

                if (daysBetween < HAWL_PERIOD_DAYS) {
                    status = ZakahStatus.HAWL_NOT_COMPLETED;
                } else {
                    status = ZakahStatus.ZAKAH_DUE;
                }
            } else {
                status = ZakahStatus.ZAKAH_DUE;
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, userId));

        ZakahIndividualRecord record = mapper.toEntity(request, user);
        record.setZakahAmount(zakahAmount);
        record.setStatus(status);

        ZakahIndividualRecord saved = repository.save(record);
        log.info("Zakah calculation completed successfully for user: {}", userId);

        return mapper.toSummaryResponse(saved);
    }

    // =================== FIND BY ID ===================
    @Override
    public ZakahIndividualRecordResponse findByIdAndUserId(Long id) {
        Long userId = userUtil.getAuthenticatedUserId();
        ZakahIndividualRecord record = repository.findByIdAndUserId(id, userId);

        if (record == null) throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);

        return mapper.toDetailedResponse(record);
    }

    // =================== FIND ALL SUMMARIES ===================
    @Override
    public List<ZakahIndividualRecordSummaryResponse> findAllSummariesByUserId() {
        Long userId = userUtil.getAuthenticatedUserId();
        return repository.findAllByUserId(userId)
                .stream()
                .map(mapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // =================== DELETE ===================
    @Override
    public void deleteByIdAndUserId(Long id) {
        Long userId = userUtil.getAuthenticatedUserId();
        ZakahIndividualRecord record = repository.findByIdAndUserId(id, userId);

        if (record == null) throw new BusinessException(ZAKAH_RECORD_NOT_FOUND, id);

        repository.deleteByIdAndUserId(id, userId);
        log.info("Deleted individual zakah record id={} for user={}", id, userId);
    }

    // =================== HELPERS ===================
    private BigDecimal calculateTotalAssets(BigDecimal cash, BigDecimal gold,
                                            BigDecimal silver, BigDecimal stocks, BigDecimal bonds) {
        BigDecimal total = BigDecimal.ZERO;
        if (cash != null) total = total.add(cash);
        if (gold != null) total = total.add(gold);
        if (silver != null) total = total.add(silver);
        if (stocks != null) total = total.add(stocks);
        if (bonds != null) total = total.add(bonds);
        return total;
    }

    private void validateRequest(ZakahIndividualRecordRequest request) {
        if (request == null) throw new BusinessException(INVALID_ZAKAH_DATA);
        if (request.getGoldPrice() == null || request.getGoldPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException(GOLD_PRICE_INVALID);
        if (request.getCalculationDate() == null) throw new BusinessException(INVALID_ZAKAH_DATA, "Calculation date required");
    }

    private void validateCalculationDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) throw new BusinessException(INVALID_BALANCE_SHEET_DATE);
    }
}
