package pl.kukla.krzys.msscssm.config.utils;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import pl.kukla.krzys.msscssm.domain.PaymentEvent;
import pl.kukla.krzys.msscssm.domain.PaymentState;

/**
 * @author Krzysztof Kukla
 */
public class MessageUtils {
    private MessageUtils() {
    }

    public static void buildAndSendMessage(StateContext<PaymentState, PaymentEvent> context, PaymentEvent paymentEvent, String paymentIdHeader) {
        Object messageHeader = context.getMessageHeader(paymentIdHeader);
        Message<PaymentEvent> eventMessage = MessageBuilder.withPayload(paymentEvent)
            .setHeader(paymentIdHeader, messageHeader)
            .build();
        context.getStateMachine().sendEvent(eventMessage);
    }

}
