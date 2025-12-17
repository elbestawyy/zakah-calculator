package ntg.project.ZakahCalculator.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
@Entity
@Table(name = "zakah_company_records")
@DiscriminatorValue("COMPANY")
@PrimaryKeyJoinColumn(name = "record_id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZakahCompanyRecord extends ZakahRecord {

        @Column(precision = 15, scale = 2)
        private BigDecimal cash;

        @Column(name = "accounts_receivable", precision = 15, scale = 2)
        private BigDecimal accountsReceivable;

        @Column(precision = 15, scale = 2)
        private BigDecimal inventory;

        @Column(precision = 15, scale = 2)
        private BigDecimal investment;

        @Column(name = "accounts_payable", precision = 15, scale = 2)
        private BigDecimal accountsPayable;

        @Column(name = "short_term_liability", precision = 15, scale = 2)
        private BigDecimal shortTermLiability;

        @Column(precision = 15, scale = 2)
        private BigDecimal expenses;
    }

