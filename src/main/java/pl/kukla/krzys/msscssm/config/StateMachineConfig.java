package pl.kukla.krzys.msscssm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;
import pl.kukla.krzys.msscssm.service.PaymentServiceImpl;

import java.util.EnumSet;
import java.util.Random;

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
            //we start with source - NEW state and when we invoke PRE_AUTHORIZE event then we are not changing PaymentState - target - PaymentState
            // will be the same
            .withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE).action(preAuthAction()).guard(paymentIdGuard())
            //on PRE_AUTH_APPROVED event change status to PRE_AUTH
            .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
            .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
            .and().withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE).action(authorizeAction())
            .and().withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
            .and().withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED);

    }


    //listener for status change
    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> stateChangeListener = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("PaymentStatus changed form-> %s to %s", from, to));
            }
        };

        config.withConfiguration().listener(stateChangeListener);
    }

    private Guard<PaymentState, PaymentEvent> paymentIdGuard() {
        return context -> {
            // if our stateMachine is missing PAYMENT_ID_HEADER then will not proceed
            return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
        };
    }

    //additional method which invokes other PaymentEvent and then sendEvent
    private Action<PaymentState, PaymentEvent> preAuthAction() {
        return context -> {
            System.out.println("PreAuth was called!!!");
            PaymentEvent paymentEvent;
            if (randomValue()) {
                System.out.println("Approved");
                paymentEvent = PaymentEvent.PRE_AUTH_APPROVED;
            } else {
                System.out.println("Declined! No credit!!!");
                paymentEvent = PaymentEvent.PRE_AUTH_DECLINED;
            }
            buildAndSendMessage(context, paymentEvent);

        };
    }

    private Action<PaymentState, PaymentEvent> authorizeAction() {
        return context -> {
            PaymentEvent paymentEvent = randomValue() ?
                PaymentEvent.AUTH_APPROVED : PaymentEvent.AUTH_DECLINED;

            buildAndSendMessage(context, paymentEvent);
        };

    }

    private boolean randomValue() {
        return new Random().nextInt(10) < 8;
    }

    private void buildAndSendMessage(StateContext<PaymentState, PaymentEvent> context, PaymentEvent paymentEvent) {
        Object messageHeader = context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER);
        Message<PaymentEvent> eventMessage = MessageBuilder.withPayload(paymentEvent)
            .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, messageHeader)
            .build();
        context.getStateMachine().sendEvent(eventMessage);
    }

}
