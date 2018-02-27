package is.valitor.library.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ingenico.pclutilities.PclUtilities;

import is.valitor.library.android.BaseRequest;
import is.valitor.library.android.CardType;
import is.valitor.library.android.CurrencyCode;
import is.valitor.library.android.Valitor;
import is.valitor.library.android.ValitorConstants;
import is.valitor.library.android.ValitorListener;
import is.valitor.library.android.request.AuthOnlyRequest;
import is.valitor.library.android.request.AuthRequest;
import is.valitor.library.android.request.LastTransactionRequest;
import is.valitor.library.android.request.LineItemPrintRequest;
import is.valitor.library.android.request.MotoRequest;
import is.valitor.library.android.request.PingRequest;
import is.valitor.library.android.request.PrintTransactionSummaryListRequest;
import is.valitor.library.android.request.RefundRequest;
import is.valitor.library.android.request.ReversalRequest;
import is.valitor.library.android.request.SendBatchRequest;
import is.valitor.library.android.request.SendLastReceiptRequest;
import is.valitor.library.android.request.TerminalPrintTransactionListRequest;
import is.valitor.library.android.request.VoiceRequest;

/**
 * Example activity that shows in a simple way how to communicate with Valitor.
 */
public class MainActivity
        extends Activity
        implements View.OnClickListener {

    // Define a field of the type Valitor.
    private Valitor mValitor = null;

    // Normally you would ask for this 4 digits. But since this is an example app write them here.
    private String mShortCardNumber = null;
    private String mLastAuthMsgId = null;

    private TextView mSelectedDevice = null;
    private TextView mLogTextView = null;
    private PclUtilities.BluetoothCompanion mDevice = null;

    private Button mButtonStart = null;
    private Button mButtonStop = null;
    private Button mButtonPing = null;
    private Button mButtonSale = null;
    private Button mButtonAuth = null;
    private Button mButtonMoto = null;
    private Button mButtonVoice = null;
    private Button mButtonRefund = null;
    private Button mButtonReversal = null;
    private Button mButtonBatch = null;
    private Button mButtonLastReceipt = null;
    private Button mButtonSummaryList = null;
    private Button mButtonTransactionList = null;
    private Button mButtonPrintString = null;
    private Button mButtonLastTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Before anything you do with Valitor, initialize the field, the first parameter is
        // the activity and the second parameter is an implementation of ValitorListener,
        // you can use ValitorListener.NOP but at least override the 3 methods shown here,
        // I consider this to be the most important ones since it notify you when the library is
        // read, unable to do anything, and when an error occurred.
        mValitor = new Valitor(this, new ValitorListener.NOP() {
            @Override
            public void onReady() {
                super.onReady();
                log("onReady");
                updateButtonsState();
            }

            @Override
            public void onServiceStopped() {
                super.onServiceStopped();
                log("onServiceStopped");
                updateButtonsState();
            }

            @Override
            public void onError(String error, Exception ex) {
                super.onError(error, ex);
                log("onError");
                log(error, ex);
            }

            @Override
            public void onServiceStarted() {
                super.onServiceStarted();
                log("onServiceStarted");
            }

            @Override
            public void onServiceBound() {
                super.onServiceBound();
                log("onServiceBound");
            }

            @Override
            public void onServiceConnected() {
                super.onServiceConnected();
                log("onServiceConnected");
            }

            @Override
            public void onServiceDisconnected() {
                super.onServiceDisconnected();
                log("onServiceDisconnected");
            }

            @Override
            public void onServiceUnbound() {
                super.onServiceUnbound();
                log("onServiceUnbound");
            }
        });

        mSelectedDevice = findViewById(R.id.main_selected_companion);
        mSelectedDevice.setOnClickListener(this);
        mLogTextView = findViewById(R.id.main_log);
        mLogTextView.setText("");
        mLogTextView.setMovementMethod(new ScrollingMovementMethod());

        mButtonStart = findViewById(R.id.main_start);
        mButtonStop = findViewById(R.id.main_stop);
        mButtonPing = findViewById(R.id.main_ping);
        mButtonSale = findViewById(R.id.main_auth);
        mButtonAuth = findViewById(R.id.main_auth_only);
        mButtonMoto = findViewById(R.id.main_moto);
        mButtonVoice = findViewById(R.id.main_voice);
        mButtonRefund = findViewById(R.id.main_refund);
        mButtonReversal = findViewById(R.id.main_reversal);
        mButtonBatch = findViewById(R.id.main_batch);
        mButtonLastReceipt = findViewById(R.id.main_last_receipt);
        mButtonSummaryList = findViewById(R.id.main_summary_list);
        mButtonTransactionList = findViewById(R.id.main_transaction_list);
        mButtonPrintString = findViewById(R.id.main_print_string);
        mButtonLastTransaction = findViewById(R.id.main_last_transaction);

        mButtonStart.setOnClickListener(this);
        mButtonStop.setOnClickListener(this);
        mButtonPing.setOnClickListener(this);
        mButtonSale.setOnClickListener(this);
        mButtonAuth.setOnClickListener(this);
        mButtonMoto.setOnClickListener(this);
        mButtonVoice.setOnClickListener(this);
        mButtonRefund.setOnClickListener(this);
        mButtonReversal.setOnClickListener(this);
        mButtonBatch.setOnClickListener(this);
        mButtonLastReceipt.setOnClickListener(this);
        mButtonSummaryList.setOnClickListener(this);
        mButtonTransactionList.setOnClickListener(this);
        mButtonPrintString.setOnClickListener(this);
        mButtonLastTransaction.setOnClickListener(this);

        updateButtonsState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDevice();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mValitor.stop();
        mDevice = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_selected_companion: {
                if (!mValitor.started()) {
                    checkDevice();
                }
            }
            case R.id.main_start: {
                log("mValitor.start();");
                mValitor.start();
                break;
            }
            case R.id.main_stop: {
                log("mValitor.stop()");
                mValitor.stop();
                break;
            }
            case R.id.main_ping: {
                log("Queueing Request " + PingRequest.class.getSimpleName());
                requestAfterPing(null);
                break;
            }
            case R.id.main_auth: {
                // Authorization
                requestAfterPing(new AuthRequest(123, CurrencyCode.ISK.mCode, false, CardType.ALL, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Auth Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Auth error", ex);
                    }
                });
                break;
            }
            case R.id.main_auth_only: {
                // Authorization Only
                requestAfterPing(new AuthOnlyRequest(123, CurrencyCode.ISK.mCode, false, CardType.ALL, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Auth Only Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Auth Only error", ex);
                    }
                });
                break;
            }
            case R.id.main_moto: {
                // MOTO
                requestAfterPing(new MotoRequest(123, CurrencyCode.ISK.mCode, false, CardType.ALL, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Moto Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Moto error", ex);
                    }
                });
                break;
            }
            case R.id.main_voice: {
                // Voice
                requestAfterPing(new VoiceRequest(123, CurrencyCode.ISK.mCode, false, CardType.ALL, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Voice Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Voice error", ex);
                    }
                });
                break;
            }
            case R.id.main_refund: {
                // Refund
                requestAfterPing(new RefundRequest(123, CurrencyCode.ISK.mCode, false, CardType.ALL, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Refund Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Refund error", ex);
                    }
                });
                break;
            }
            case R.id.main_reversal: {
                // Reversal
                requestAfterPing(new ReversalRequest(123, CurrencyCode.ISK.mCode, false, CardType.ALL, true, mShortCardNumber, mLastAuthMsgId) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Reversal Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Reversal error", ex);
                    }
                });
                break;
            }
            case R.id.main_batch: {
                // Send Batch
                requestAfterPing(new SendBatchRequest(false, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Batch Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Batch error", ex);
                    }
                });
                break;
            }
            case R.id.main_last_receipt: {
                // Send Last Receipt
                requestAfterPing(new SendLastReceiptRequest(false, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Last Receipt Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Last Receipt error", ex);
                    }
                });
                break;
            }
            case R.id.main_summary_list: {
                // Print Transaction Summary List
                requestAfterPing(new PrintTransactionSummaryListRequest(false, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Print Transaction Summary List Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Print Transaction Summary List error", ex);
                    }
                });
                break;
            }
            case R.id.main_transaction_list: {
                // Terminal Print Transaction List
                requestAfterPing(new TerminalPrintTransactionListRequest(false, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Terminal Print Transaction List Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Terminal Print Transaction List error", ex);
                    }
                });
                break;
            }
            case R.id.main_print_string: {
                // Line Item Print
                requestAfterPing(new LineItemPrintRequest(false, true, "[L][C]Halló Heimur;;;;;;") {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Line Item Print Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Line Item Print error", ex);
                    }
                });
                break;
            }
            case R.id.main_last_transaction: {
                // Last Transaction
                requestAfterPing(new LastTransactionRequest(false, true) {
                    @Override
                    public void onResponse() {
                        log(this);
                        retrieveLastAuthMsgId(this);
                    }

                    // This one runs in a background thread.
                    @Override
                    public void onStatus(String status) {
                        log("Last Transaction Status = " + status);
                    }

                    @Override
                    public void onError(Exception ex) {
                        log("Last Transaction error", ex);
                    }
                });
                break;
            }
        }
    }

    /**
     * "Wrapper" to execute a request after a ping.
     *
     * @param request The request to be executed after a successful ping.
     */
    private void requestAfterPing(final BaseRequest request) {
        mValitor.execute(new PingRequest() {
            @Override
            public void onResponse() {
                log(this);
                if (request != null) {
                    log("Queueing Request " + request.getClass().getSuperclass().getSimpleName());
                    mValitor.execute(request);
                }
            }

            @Override
            public void onError(Exception ex) {
                log("Ping error", ex);
            }
        });
    }

    private void checkDevice() {
        if (mDevice == null) {
            new CompanionPickerDialog(this, mValitor, new CompanionListener() {
                @Override
                public void onCompanionClicked(PclUtilities.BluetoothCompanion companion) {
                    // Activate the companion.
                    if (mValitor.activateDevice(companion)) {
                        mDevice = companion;
                        updateButtonsState();
                        log("Device activated");
                    } else {
                        mDevice = null;
                        log("Failed to activate companion");
                    }
                }
            }).show();
        }
    }

    private void retrieveLastAuthMsgId(BaseRequest request) {
        ArrayMap<String, String> inputExtras = request.getInputExtras();
        String approved = inputExtras.get(ValitorConstants.APPROVED);
        if (approved != null && approved.compareTo("1") == 0) {
            mLastAuthMsgId = inputExtras.get(ValitorConstants.AUTH_MSG_ID);
        } else {
            mLastAuthMsgId = null;
        }
        updateButtonsState();
    }

    @SuppressLint("SetTextI18n")
    private void updateButtonsState() {
        if (mDevice != null) {
            mSelectedDevice.setText(mDevice.getName() + " selected");
        } else {
            mSelectedDevice.setText("No Device Selected");
        }

        boolean start = mDevice != null && !mValitor.started();
        boolean ping = false;
        boolean sale = false;
        boolean auth = false;
        boolean moto = false;
        boolean voice = false;
        boolean refund = false;
        boolean reversal = false;
        boolean batch = false;
        boolean lastReceipt = false;
        boolean summaryList = false;
        boolean transactionList = false;
        boolean printString = false;
        boolean lastTransaction = false;

        if (mDevice != null && mValitor.started()) {
            start = false;
            ping = true;
            sale = true;
            auth = true;
            moto = true;
            voice = true;
            refund = true;
            reversal = mShortCardNumber != null && mLastAuthMsgId != null;
            batch = true;
            lastReceipt = true;
            summaryList = true;
            transactionList = true;
            printString = true;
            lastTransaction = true;
        }

        mButtonStart.setEnabled(start);
        mButtonStop.setEnabled(!start);
        mButtonPing.setEnabled(ping);
        mButtonSale.setEnabled(sale);
        mButtonAuth.setEnabled(auth);
        mButtonMoto.setEnabled(moto);
        mButtonVoice.setEnabled(voice);
        mButtonRefund.setEnabled(refund);
        mButtonReversal.setEnabled(reversal);
        mButtonBatch.setEnabled(batch);
        mButtonLastReceipt.setEnabled(lastReceipt);
        mButtonSummaryList.setEnabled(summaryList);
        mButtonTransactionList.setEnabled(transactionList);
        mButtonPrintString.setEnabled(printString);
        mButtonLastTransaction.setEnabled(lastTransaction);
    }

    private <T extends BaseRequest> void log(T request) {
        StringBuilder result = new StringBuilder();
        // This works as expected because all the request are anonymous inside MainActivity.
        result.append(request.getClass().getSuperclass().getSimpleName());
        result.append(" Processed");

        ArrayMap<String, String> inputExtras = request.getInputExtras();
        int size = inputExtras.size();
        for (int i = 0; i < size; ++i) {
            result.append("\n");
            result.append(inputExtras.keyAt(i));
            result.append(" = ");
            result.append(inputExtras.valueAt(i));
        }

        log(result.toString());
    }

    private void log(String message) {
        appendLog(message);
        Log.d("ValitorTest", message);
    }

    private void log(String message, Throwable throwable) {
        appendLog(message);
        Log.d("ValitorTest", message, throwable);
    }

    private void appendLog(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogTextView.append(message);
                mLogTextView.append("\n");
                int scroll = mLogTextView.getLayout().getLineTop(mLogTextView.getLineCount()) -
                        mLogTextView.getHeight();
                mLogTextView.scrollTo(0, scroll > 0 ? scroll : 0);
            }
        });
    }

}
