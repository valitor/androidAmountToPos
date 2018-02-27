package is.valitor.library.android;

import java.lang.reflect.Field;

import static is.valitor.library.android.ValitorUtils.checkNotNull;

/**
 * Request that supports request that need a Msg Type and a Msg Code, usually this are
 * "Network Management" messages.
 */
public abstract class MsgCodeRequest
        extends PrintStatusRequest {

    @ValitorProperty(name = ValitorConstants.MSG_CODE)
    public final String mMsgCode;

    public MsgCodeRequest(MsgType type, MsgCode code, boolean print, boolean status) {
        super(type, 0, print, status);
        mMsgCode = checkNotNull(code, "invalid code").mType;
    }

    @Override
    protected boolean allowField(Field field, String name) {
        if (name.compareTo(ValitorConstants.MSG_CODE) == 0 ||
                name.compareTo(ValitorConstants.AMOUNT) == 0) {
            return false;
        }
        return super.allowField(field, name);
    }

    @Override
    protected void appendPreFields(StringBuilder builder) {
        super.appendPreFields(builder);
        appendField(builder, ValitorConstants.MSG_CODE, mMsgCode);
    }

}
