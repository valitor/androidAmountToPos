package is.valitor.library.android;

import java.lang.reflect.Field;

/**
 * Request that supports messages that need a check sum.
 */
public abstract class ChecksumRequest
        extends BaseRequest {

    @ValitorProperty(name = ValitorConstants.AMOUNT)
    public final String mAmount;

    @ValitorProperty(name = ValitorConstants.RAND)
    public final String mRand;

    @ValitorProperty(name = ValitorConstants.CHECK)
    public final String mCheck;

    public ChecksumRequest(MsgType type, int amount) {
        super(type);
        mAmount = ValitorUtils.valitorAmount(amount);
        int rand = Valitor.getRand();
        mRand = String.valueOf(rand);
        mCheck = String.valueOf(Valitor.checksum(Integer.parseInt(mMsgType, 10),
                Integer.parseInt(mAmount), rand));
    }

    @Override
    protected boolean allowField(Field field, String name) {
        if (name.compareTo(ValitorConstants.RAND) == 0 ||
                name.compareTo(ValitorConstants.CHECK) == 0) {
            return false;
        }
        return super.allowField(field, name);
    }

    @Override
    protected void appendPostFields(StringBuilder builder) {
        appendField(builder, ValitorConstants.RAND, mRand);
        appendField(builder, ValitorConstants.CHECK, mCheck);
        super.appendPostFields(builder);
    }

}
