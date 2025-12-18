package ntg.project.ZakahCalculator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
}

