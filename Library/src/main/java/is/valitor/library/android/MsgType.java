package is.valitor.library.android;

public enum MsgType {

    AUTH_REQUEST("0100"),
    MOTO_REQUEST("0101"),
    AUTH_ONLY_REQUEST("0102"),
    VOICE_REQUEST("0103"),
    AUTH_REFUND_REQUEST("0200"),
    AUTH_REVERSAL_REQUEST("0400"),
    // CARD_READ_REQUEST("0700"),
    NETWORK_MANAGEMENT_REQUEST("0800");

    public final String mType;

    MsgType(String type) {
        mType = type;
    }

}
