package ntg.project.ZakahCalculator.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZakahCompanyRecordRequest {
    @NotBlank(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PastOrPresent(message = "لايمكن ان يكون التاريخ فى المستقبل.")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate balance_sheet_date;

    //Assets
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal cashEquivalents;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal investment;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal inventory;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal accountsReceivable;


    //Liabilities
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal accountsPayable;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal accruedExpenses;
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal shortTermLiability;
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal yearly_long_term_liabilities;
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal goldPrice;
}
