package is.valitor.library.android.request;

import java.util.concurrent.TimeUnit;

import is.valitor.library.android.BaseRequest;
import is.valitor.library.android.MsgCode;
import is.valitor.library.android.MsgType;
import is.valitor.library.android.ValitorConstants;
import is.valitor.library.android.ValitorProperty;

public abstract class PingRequest
        extends BaseRequest {

    @ValitorProperty(name = ValitorConstants.MSG_CODE)
    public final String mMsgCode = MsgCode.PING.mType;

    public PingRequest() {
        super(MsgType.NETWORK_MANAGEMENT_REQUEST);
    }

    @Override
    public int getTimeout() {
        return (int) TimeUnit.SECONDS.toMillis(5);
    }

}
