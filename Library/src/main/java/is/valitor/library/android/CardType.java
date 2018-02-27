package is.valitor.library.android;

public enum CardType {

    ALL("0"),
    DEBIT("1"),
    CREDIT("2");

    public final String mType;

    CardType(String type) {
        mType = type;
    }

}
