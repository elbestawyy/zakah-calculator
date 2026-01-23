package ntg.project.ZakahCalculator.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZakahCompanyRecordRequest {
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PastOrPresent(message = "لايمكن ان يكون التاريخ فى المستقبل.")
    private LocalDate balanceSheetDate;
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal goldPrice;

//    //Income Statment Items
//    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
//    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
//    private BigDecimal netProfit;//new item

    //Assets
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal cashEquivalents;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal accountsReceivable;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal inventory;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal investment;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal generatingFixedAssets;//new item

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
    private BigDecimal yearlyLongTermLiabilities;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal contraAssets; //new item

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal provisionsUnderLiabilities;//new item


}
