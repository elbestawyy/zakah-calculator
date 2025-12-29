package ntg.project.ZakahCalculator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

//TODO
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZakahIndividualRecordRequest {

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal cash;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal gold;
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal silver;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal bonds;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal goldPrice;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private LocalDate calculationDate;

    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @PositiveOrZero(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal stocks;

}
