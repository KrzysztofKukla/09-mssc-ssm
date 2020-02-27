package pl.kukla.krzys.msscssm.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() throws Exception {
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.start();

        System.out.println(stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        System.out.println(stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        System.out.println(stateMachine.getState().toString());
    }

}