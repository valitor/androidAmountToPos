package is.valitor.library.android.request;

import is.valitor.library.android.MsgCode;
import is.valitor.library.android.MsgCodeRequest;
import is.valitor.library.android.MsgType;

public abstract class LastTransactionRequest
        extends MsgCodeRequest {

    public LastTransactionRequest(boolean print, boolean status) {
        super(MsgType.NETWORK_MANAGEMENT_REQUEST, MsgCode.LAST_TRANSACTION, print, status);
    }

}
