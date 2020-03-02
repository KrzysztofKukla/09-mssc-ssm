package pl.kukla.krzys.msscssm.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;
import pl.kukla.krzys.msscssm.domain.Payment;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;

import java.math.BigDecimal;

import static pl.kukla.krzys.msscssm.domain.PaymentState.AUTH;
import static pl.kukla.krzys.msscssm.domain.PaymentState.AUTH_ERROR;
import static pl.kukla.krzys.msscssm.domain.PaymentState.PRE_AUTH;
import static pl.kukla.krzys.msscssm.domain.PaymentState.PRE_AUTH_ERROR;

/**
 * @author Krzysztof Kukla
 */
@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
            .amount(new BigDecimal("12.99"))
            .build();
    }

    @Transactional
    @Test
    void newStateTestChange() {
        Payment savedPayment = paymentService.newPayment(this.payment);
        Assertions.assertEquals(PaymentState.NEW, savedPayment.getPaymentState());

        Long paymentId = savedPayment.getId();
        StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.preAuth(paymentId);
        PaymentState currentState = stateMachine.getState().getId();
        org.assertj.core.api.Assertions.assertThat(currentState).isIn(PRE_AUTH, PRE_AUTH_ERROR);
    }

    @Transactional
    @RepeatedTest(10)
    void preAuthStateChange() throws Exception {
        //given
        Payment savedPayment = paymentService.newPayment(this.payment);
        StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.preAuth(savedPayment.getId());

        //when
        PaymentState currentState = stateMachine.getState().getId();
        Assumptions.assumeTrue(currentState == PRE_AUTH, "Current state is-> " + currentState);

        //then
        stateMachine = paymentService.authorizePayment(savedPayment.getId());
        currentState = stateMachine.getState().getId();
        org.assertj.core.api.Assertions.assertThat(currentState).isIn(AUTH, AUTH_ERROR);

    }

}