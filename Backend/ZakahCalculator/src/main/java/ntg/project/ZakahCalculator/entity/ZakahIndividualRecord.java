package ntg.project.ZakahCalculator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "zakah_individual_records")
@DiscriminatorValue("INDIVIDUAL")
@PrimaryKeyJoinColumn(name = "record_id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZakahIndividualRecord extends ZakahRecord {

    @Column(precision = 15, scale = 2)
    private BigDecimal cash;

    @Column(precision = 15, scale = 2)
    private BigDecimal gold;

    @Column(precision = 15, scale = 2)
    private BigDecimal silver;

    @Column(precision = 15, scale = 2)
    private BigDecimal stocks;

    @Column(precision = 15, scale = 2)
    private BigDecimal bonds;

    @NotNull(message = "Calculation date is required")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate calculationDate;

    @NotNull(message = "User ID is required")
    private Long userId;
}

