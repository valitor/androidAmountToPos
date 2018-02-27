package is.valitor.library.android.request;

import is.valitor.library.android.MsgCode;
import is.valitor.library.android.MsgCodeRequest;
import is.valitor.library.android.MsgType;

public abstract class PrintTransactionSummaryListRequest
        extends MsgCodeRequest {

    public PrintTransactionSummaryListRequest(boolean print, boolean status) {
        super(MsgType.NETWORK_MANAGEMENT_REQUEST,
                MsgCode.PRINT_TRANSACTION_SUMMARY_LIST,
                print,
                status);
    }

}
