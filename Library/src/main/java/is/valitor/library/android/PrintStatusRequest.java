package is.valitor.library.android;

import android.util.ArrayMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Request that supports reading status message received from the POSI.
 */
public abstract class PrintStatusRequest
        extends ChecksumRequest {

    @ValitorProperty(name = ValitorConstants.PRINT)
    public final String mPrint;

    @ValitorProperty(name = ValitorConstants.STATUS)
    public final String mStatus;

    public PrintStatusRequest(MsgType type, int amount, boolean print, boolean status) {
        super(type, amount);
        mPrint = print ? "1" : "0";
        mStatus = status ? "1" : "0";
    }

    @Override
    public void execute(BufferedWriter output, BufferedReader input)
            throws IOException {
        constructFields(getClass());

        String message = toRequestString();
        send(output, message);

        String type;
        String code;
        String status;
        String received;
        ArrayMap<String, String> extras;
        while (true) {
            received = receive(input);
            extras = new ArrayMap<>();
            parseReceived(received, extras);
            type = extras.get(ValitorConstants.MSG_TYPE);
            code = extras.get(ValitorConstants.MSG_CODE);
            // We loop trying to read messages from the POSI and checking if the message is a
            // status message, if it's then we notify and loop again, but if it's not then
            // we finish the loop.
            if (type != null && code != null &&
                    type.compareTo(ResponseMsgType.NETWORK_MANAGEMENT_RESPONSE.mType) == 0 &&
                    code.compareTo(MsgCode.STATUS_MESSAGE.mType) == 0) {
                status = extras.get(ValitorConstants.STATUS);
                onStatus(status);
            } else {
                mInputExtras.putAll(extras);
                break;
            }
        }
    }

    public void onStatus(String status) {
        // This one runs in a background thread.
    }

}
