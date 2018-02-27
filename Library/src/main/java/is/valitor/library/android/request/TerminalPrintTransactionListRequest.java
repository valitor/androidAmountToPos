package is.valitor.library.android.request;

import is.valitor.library.android.MsgCode;
import is.valitor.library.android.MsgCodeRequest;
import is.valitor.library.android.MsgType;

public abstract class TerminalPrintTransactionListRequest
        extends MsgCodeRequest {

    public TerminalPrintTransactionListRequest(boolean print, boolean status) {
        super(MsgType.NETWORK_MANAGEMENT_REQUEST,
                MsgCode.TERMINAL_PRINT_TRANSACTION_LIST,
                print,
                status);
    }

}
