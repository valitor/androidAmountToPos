package is.valitor.library.android.request;

import is.valitor.library.android.MessageDeliveredRequest;
import is.valitor.library.android.CardType;
import is.valitor.library.android.MsgType;

public abstract class VoiceRequest
        extends MessageDeliveredRequest {

    public VoiceRequest(int amount,
                        String currency,
                        boolean print,
                        CardType cardType,
                        boolean status) {
        super(MsgType.VOICE_REQUEST, amount, currency, print, cardType, status);
    }

}
