package pl.kukla.krzys.msscssm.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.msscssm.domain.Payment;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;
import pl.kukla.krzys.msscssm.repository.PaymentRepository;
import pl.kukla.krzys.msscssm.service.PaymentServiceImpl;

import java.util.Optional;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    //is invoked before state change
    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState,
        PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
                .ifPresent(paymentId -> {
                    //whenever state in state machine is changing we are setting paymentState for payment ( getting by paymentId )
                    // and persist/save to database
                    Payment payment = paymentRepository.getOne(paymentId);
                    payment.setPaymentState(state.getId());
                    paymentRepository.save(payment);
                });
        });
    }

}
