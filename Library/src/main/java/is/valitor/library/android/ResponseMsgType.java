package is.valitor.library.android;

public enum ResponseMsgType {

    AUTH_RESPONSE("0110"),
    MOTO_RESPONSE("0111"),
    AUTH_ONLY_RESPONSE("0112"),
    VOICE_RESPONSE("0113"),
    AUTH_REFUND_RESPONSE("0210"),
    AUTH_REVERSAL_RESPONSE("0410"),
    // CARD_READ_RESPONSE("0700"),
    NETWORK_MANAGEMENT_RESPONSE("0810");

    public final String mType;

    ResponseMsgType(String type) {
        mType = type;
    }

}
