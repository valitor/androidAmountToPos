package is.valitor.library.android;

import android.util.ArrayMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import static is.valitor.library.android.ValitorUtils.checkNotNull;

/**
 * Request that requires a Message Delivered response if successful.
 */
public abstract class MessageDeliveredRequest
        extends PrintStatusRequest {

    @ValitorProperty(name = ValitorConstants.CURRENCY)
    public final String mCurrency;

    @ValitorProperty(name = ValitorConstants.CARD_TYPE)
    public final String mCardType;

    public MessageDeliveredRequest(MsgType type,
                                   int amount,
                                   String currency,
                                   boolean print,
                                   CardType cardType,
                                   boolean status) {
        super(type, amount, print, status);
        mCurrency = checkNotNull(currency, "invalid currency code");
        mCardType = checkNotNull(cardType, "invalid card type").mType;
    }

    private final ArrayMap<String, String> mMessageDeliveredExtras = new ArrayMap<>();

    /**
     * Retrieve an map that contains the message delivered response.
     *
     * @return An ArrayMap<String, String> with the message delivered response.
     */
    public ArrayMap<String, String> getMessageDeliveredExtras() {
        return new ArrayMap<>(mMessageDeliveredExtras);
    }

    @Override
    public void execute(BufferedWriter output, BufferedReader input)
            throws IOException {
        // First perform the normal flow from super.
        super.execute(output, input);
        // We then check if we need to "confirm" the request.
        if (needsConfirmation()) {
            confirm(output, input);
        }
    }

    protected boolean needsConfirmation() {
        // We need to "confirm" if we were approved.
        String approved = mInputExtras.get(ValitorConstants.APPROVED);
        return approved != null && approved.compareTo("1") == 0;
    }

    private void confirm(BufferedWriter output, BufferedReader input)
            throws IOException {
        // Send a Message Delivered that "confirms" the request.
        int rand = Valitor.getRand();
        String confirmRand = String.valueOf(rand);
        String confirmCheck = String.valueOf(Valitor.checksum(
                Integer.parseInt(MsgType.NETWORK_MANAGEMENT_REQUEST.mType),
                Integer.parseInt(mAmount), rand));
        StringBuilder builder = new StringBuilder();
        appendField(builder, ValitorConstants.MSG_TYPE, MsgType.NETWORK_MANAGEMENT_REQUEST.mType);
        appendField(builder, ValitorConstants.MSG_CODE, MsgCode.MESSAGE_DELIVERED.mType);
        appendField(builder, ValitorConstants.RAND, confirmRand);
        appendField(builder, ValitorConstants.CHECK, confirmCheck);
        send(output, builder.toString());

        String receive = receive(input);
        parseReceived(receive, mMessageDeliveredExtras);
    }

}
