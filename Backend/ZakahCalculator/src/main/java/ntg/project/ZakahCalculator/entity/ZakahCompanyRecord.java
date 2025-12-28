package ntg.project.ZakahCalculator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "zakah_company_records")
@DiscriminatorValue("COMPANY")
@PrimaryKeyJoinColumn(name = "record_id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZakahCompanyRecord extends ZakahRecord {

    // Assets
    @Column(name = "cash_equivalents", precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal cashEquivalents;

    @Column(name = "accounts_receivable", precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal accountsReceivable;

    @Column(precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal inventory;

    @Column(precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal investment;

    // Liabilities
    @Column(name = "accounts_payable", precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal accountsPayable;

    @Column(name = "short_term_liability", precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal shortTermLiability;

    @Column(name = "accrued_expenses", precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal accruedExpenses;

    // yearly_portion_of_long_term_liabilities
    @Column(name = "yearly_long_term_liabilities", precision = 15, scale = 2)
    @NotNull(message = "هذا الحقل مطلوب، من فضلك لا تتركه فارغًا.")
    @Positive(message = "من فضلك أدخل رقمًا أكبر من أو يساوي صفر.")
    private BigDecimal yearlyLongTermLiabilities;

    // Balance sheet Data
    @Column(name = "balance_sheet_date")
    @NotBlank(message = "")
    @PastOrPresent(message = "لايمكن ان يكون التاريخ فى المستقبل.")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate balanceSheetDate;
}