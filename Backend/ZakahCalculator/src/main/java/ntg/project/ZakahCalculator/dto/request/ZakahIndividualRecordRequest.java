package ntg.project.ZakahCalculator.dto.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "This field can't be Empty")
    private BigDecimal Cash;

    @NotBlank(message = "This field can't be Empty")
    private BigDecimal Gold;
    @NotBlank(message = "This field can't be Empty")
    private BigDecimal Silver;

    @NotBlank(message = "This field can't be Empty")
    private BigDecimal Bonds;

    @NotBlank(message = "This field can't be Empty")
    private Long userId;

    @NotBlank(message = "This field can't be Empty")
    private BigDecimal goldPrice;

    @NotBlank(message = "This field can't be Empty")
    private BigDecimal silverPrice;

    @NotBlank(message = "This field can't be Empty")
    private LocalDate CalculationDate;

    @NotBlank(message = "This field can't be Empty")
    private BigDecimal stocks;

}
