package is.valitor.library.android.request;

import is.valitor.library.android.MessageDeliveredRequest;
import is.valitor.library.android.CardType;
import is.valitor.library.android.MsgType;
import is.valitor.library.android.ValitorConstants;
import is.valitor.library.android.ValitorProperty;

import static is.valitor.library.android.ValitorUtils.checkStringNotEmpty;

public abstract class ReversalRequest
        extends MessageDeliveredRequest {

    @ValitorProperty(name = ValitorConstants.CARD_NUMBER_SHORT)
    public final String mCardNumberShort;

    @ValitorProperty(name = ValitorConstants.AUTH_MSG_ID)
    public final String mAuthMsgId;

    public ReversalRequest(int amount,
                           String currency,
                           boolean print,
                           CardType cardType,
                           boolean status,
                           String cardNumberShort,
                           String authMsgId) {
        super(MsgType.AUTH_REVERSAL_REQUEST, amount, currency, print, cardType, status);
        cardNumberShort = checkStringNotEmpty(cardNumberShort, "invalid card number");
        if (cardNumberShort.length() != 4) {
            throw new IllegalArgumentException("invalid card number, length must be 4");
        }
        mCardNumberShort = cardNumberShort;
        mAuthMsgId = checkStringNotEmpty(authMsgId, "invalid auth msg id");
    }

    @Override
    protected boolean needsConfirmation() {
        return false;
    }

}
