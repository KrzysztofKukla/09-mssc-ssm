package pl.kukla.krzys.msscssm.config.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;

import java.util.Random;

import static pl.kukla.krzys.msscssm.domain.PaymentEvent.PRE_AUTH_APPROVED;
import static pl.kukla.krzys.msscssm.domain.PaymentEvent.PRE_AUTH_DECLINED;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PreAuthApprovedAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("PreAuthApprovedAction");
//        PaymentEvent paymentEvent = randomPaymentEvent();
//        MessageUtils.buildAndSendMessage(context, paymentEvent, PaymentServiceImpl.PAYMENT_ID_HEADER);
    }

    private static PaymentEvent randomPaymentEvent() {
        return new Random().nextInt(10) < 8 ?
            PRE_AUTH_APPROVED : PRE_AUTH_DECLINED;
    }

}
