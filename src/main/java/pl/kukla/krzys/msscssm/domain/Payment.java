package pl.kukla.krzys.msscssm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author Krzysztof Kukla
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    //it creates Varchar in database and name of enumeration will be there
    //if we omit that the numbered value will be there
    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;

    private BigDecimal amount;

}
