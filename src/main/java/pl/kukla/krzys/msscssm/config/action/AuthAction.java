package pl.kukla.krzys.msscssm.config.action;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.msscssm.config.utils.MessageUtils;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;
import pl.kukla.krzys.msscssm.service.PaymentServiceImpl;

import java.util.Random;

import static pl.kukla.krzys.msscssm.domain.PaymentEvent.AUTH_APPROVED;
import static pl.kukla.krzys.msscssm.domain.PaymentEvent.AUTH_DECLINED;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
public class AuthAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        PaymentEvent paymentEvent = randomPaymentEvent();
        MessageUtils.buildAndSendMessage(context, paymentEvent, PaymentServiceImpl.PAYMENT_ID_HEADER);
    }

    private static PaymentEvent randomPaymentEvent() {
        return new Random().nextInt(10) < 8 ?
            AUTH_APPROVED : AUTH_DECLINED;
    }

}
