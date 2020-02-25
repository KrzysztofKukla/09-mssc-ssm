package pl.kukla.krzys.msscssm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kukla.krzys.msscssm.domain.Payment;

/**
 * @author Krzysztof Kukla
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
