package is.valitor.library.android;

public enum MsgCode {

    SEND_BATCH("100"),
    SEND_LAST_RECEIPT("200"),
    PRINT_TRANSACTION_SUMMARY_LIST("201"),
    TERMINAL_PRINT_TRANSACTION_LIST("202"),
    LAST_RECEIPT("203"),
    PING("300"),
    LINE_ITEM_PRINT("500"),
    LAST_TRANSACTION("600"),
    MESSAGE_DELIVERED("700"),
    STATUS_MESSAGE("900");

    public final String mType;

    MsgCode(String type) {
        mType = type;
    }

}
