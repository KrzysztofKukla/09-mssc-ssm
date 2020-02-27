package pl.kukla.krzys.msscssm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
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

    //events can cause state change, but event don't have to cause state change
    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
            //we start with NEW state and when we invoke PRE_AUTHORIZE event then we are not changing PaymentState - PaymentState will be the same
            .withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
            .and()
            //on PRE_AUTH_APPROVED event change status to PRE_AUTH
            .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
            .and()
            .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED);
        //if we will specify any transition PaymentState then PaymentState will stay the same

    }

}
