package pl.kukla.krzys.msscssm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;

import java.util.EnumSet;

/**
 * @author Krzysztof Kukla
 */
@Configuration
@EnableStateMachineFactory // it scans Spring Components to generate ( create ) State Machine
@Slf4j
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        //here we set up StateMachine configuration about our State Machine
        states.withStates()
            //initial State Machine with NEW state
            .initial(PaymentState.NEW)
            .states(EnumSet.allOf(PaymentState.class))
            .end(PaymentState.AUTH) //everything OK
            .end(PaymentState.PRE_AUTH_ERROR) //error with Credit Card
            .end(PaymentState.AUTH_ERROR); //
    }

}
