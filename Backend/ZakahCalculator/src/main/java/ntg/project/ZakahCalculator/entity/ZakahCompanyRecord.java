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
//    //Items from Income Statment
//    @Column(precision = 15, scale = 2)
//    private BigDecimal netProfit;

    // Assets
    @Column(name = "cash_equivalents", precision = 15, scale = 2)
    private BigDecimal cashEquivalents; // "النقد وما في حكمه (شهادات إيداع، أذون خزانة)"

    @Column(name = "accounts_receivable", precision = 15, scale = 2)
    private BigDecimal accountsReceivable; // "الذمم المدينة (مستحقات على العملاء)"

    @Column(precision = 15, scale = 2)
    private BigDecimal inventory; // "المخزون (بضاعة تامة، تحت التصنيع، مواد خام)"

    @Column(precision = 15, scale = 2)
    private BigDecimal investment; // "الاستثمارات طويلة الأجل (أسهم، سندات، شركات تابعة)"

    @Column(precision = 15, scale = 2)
    private BigDecimal generatingFixedAssets;

    // Liabilities
    @Column(name = "accounts_payable", precision = 15, scale = 2)
    private BigDecimal accountsPayable; // "الذمم الدائنة (مستحقات للموردين)"

    @Column(name = "short_term_liability", precision = 15, scale = 2)
    private BigDecimal shortTermLiability; // "الالتزامات قصيرة الأجل (قروض سنة أو أقل)"

    @Column(name = "accrued_expenses", precision = 15, scale = 2)
    private BigDecimal accruedExpenses; // "المصروفات المستحقة (رواتب، ضرائب، فوائد متراكمة)"

    // yearly_portion_of_long_term_liabilities
    @Column(name = "yearly_long_term_liabilities", precision = 15, scale = 2)
    private BigDecimal yearlyLongTermLiabilities; // "حصَّة السنة من الالتزامات طويلة الأجل"

    @Column(precision = 15, scale = 2)
    private BigDecimal contraAssets;

    @Column(precision = 15, scale = 2)
    private BigDecimal provisionsUnderLiabilities;

    // Balance sheet Data
    @Column(name = "balance_sheet_date")
    private LocalDate balanceSheetDate; // "تاريخ إعداد الميزانية (آخر يوم في الفترة المحاسبية)"

    @Column(name= "total_assets")
    private BigDecimal totalAssets;

    @Column(name= "total_liabilities")
    private BigDecimal totalLiabilities;

    @Column(name= "zakah_pool")
    private BigDecimal zakahPool;

}