package is.valitor.library.android.request;

import is.valitor.library.android.MsgCode;
import is.valitor.library.android.MsgCodeRequest;
import is.valitor.library.android.MsgType;
import is.valitor.library.android.ValitorConstants;
import is.valitor.library.android.ValitorProperty;

import static is.valitor.library.android.ValitorUtils.checkStringNotEmpty;

public abstract class LineItemPrintRequest
        extends MsgCodeRequest {

    @ValitorProperty(name = ValitorConstants.MISC_PRINT)
    private final String mMiscPrint;

    public LineItemPrintRequest(boolean print, boolean status, String line) {
        super(MsgType.NETWORK_MANAGEMENT_REQUEST, MsgCode.LINE_ITEM_PRINT, print, status);
        mMiscPrint = checkStringNotEmpty(line, "invalid line");
    }

}
