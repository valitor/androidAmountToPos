package is.valitor.library.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.ingenico.pclservice.PclService;
import com.ingenico.pclutilities.PclUtilities;
import com.ingenico.pclutilities.SslObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static is.valitor.library.android.ValitorUtils.checkNotNull;

public class Valitor {

    static {
        System.loadLibrary("valitor");
    }

    /**
     * Calculates a checksum with Valitor's formula.
     *
     * @param type   The type of the request.
     * @param amount The amount of the request.
     * @param rand   A random number between 1 and 999 inclusive.
     * @return The checksum for the supplied values.
     */
    static public native int checksum(int type, int amount, int rand); // TODO amount double

    /**
     * Generate a random number between 1 and 999 inclusive.
     *
     * @return A random number between 1 and 999 inclusive.
     */
    static public int getRand() {
        return sRand.nextInt(999) + 1;
    }

    static private Random sRand = new Random();

    /**
     * Log a message if logs are enabled.
     *
     * @param message The message to be logged.
     */
    static public void log(String message) {
        if (ValitorConfig.ENABLE_LOG) {
            Log.d("Valitor", message);
        }
    }

    /**
     * Log a message and a throwable if logs are enabled.
     *
     * @param message   The message to be logged.
     * @param throwable The throwable to include with the message.
     */
    static public void log(String message, Throwable throwable) {
        if (ValitorConfig.ENABLE_LOG) {
            Log.d("Valitor", message, throwable);
        }
    }

    // --- public --- //

    /**
     * This constructor has to be called after the onCreate of the activity, if not it will crash
     * because PclUtilities makes a call to getSystemService which asserts that the state.
     * <p>
     * All the methods of this class must be called after ValitorListener.onReady and before
     * ValitorListener.onServiceUnbound otherwise crashes might occur.
     *
     * @param activity The activity we'll use for internal purposes and we will keep a reference to.
     * @param listener An implementation of ValitorListener that will receive events generated from
     *                 this class.
     */
    public Valitor(Activity activity, ValitorListener listener) {
        mActivity = checkNotNull(activity, "activity can't be null");
        mListener = checkNotNull(listener, "listener can't be null");
        mPclUtilities = new PclUtilities(mActivity,
                ValitorConfig.getPackageName(),
                ValitorConfig.FILE_NAME);
    }

    /**
     * Retrieve the list of available devices.
     *
     * @return A list containing the available devices or null.
     */
    public Set<PclUtilities.BluetoothCompanion> getDevices() {
        return mPclUtilities.GetPairedCompanions();
    }

    /**
     * Activate a pcl bluetooth companion.
     *
     * @param device The device to activate.
     * @return True if the device was successfully activated, false otherwise.
     */
    public boolean activateDevice(PclUtilities.BluetoothCompanion device) {
        return checkNotNull(device).activate();
    }

    /**
     * Quick test to see if the library is ready.
     *
     * @return True if the library is ready to make requests.
     */
    public boolean started() {
        return mRequestsHandler != null;
    }

    /**
     * Initiate some of the required components for this library to work.
     */
    public void start() {
        if (mPclUtilities.getActivatedCompanion() == null) {
            mListener.onError(ValitorListener.NO_ACTIVE_DEVICE, null);
        } else if (!mStarted) {
            Intent intent = getPclIntent(mActivity);
            mStarted = mActivity.startService(intent) != null;
            if (mStarted) {
                mListener.onServiceStarted();
                mBound = mActivity.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                if (mBound) {
                    mListener.onServiceBound();
                } else {
                    mListener.onError(ValitorListener.BIND_FAILED, null);
                    mStarted = !mActivity.stopService(intent);
                }
            } else {
                mListener.onError(ValitorListener.START_FAILED, null);
            }
        }
    }

    /**
     * Stop all components, and dispose them, that this library created for its use.
     */
    public void stop() {
        if (mStarted) {
            if (mStateReceiver != null) {
                mActivity.unregisterReceiver(mStateReceiver);
                mStateReceiver = null;
            }

            if (mBound) {
                mActivity.unbindService(mServiceConnection);
            }
            mBound = false;
            mListener.onServiceUnbound();

            mPclService = null;
            stopThread();

            Intent intent = getPclIntent(mActivity);
            mStarted = !mActivity.stopService(intent);
            if (mStarted) {
                mListener.onError(ValitorListener.STOP_FAILED, null);
            } else {
                mListener.onServiceStopped();
            }
        }
    }

    /**
     * If possible, adds a request to the queue to be processed later.
     *
     * @param request The request to be executed later.
     */
    public void execute(BaseRequest request) {
        checkNotNull(request);
        if (mRequestsHandler != null) {
            Message msg = mRequestsHandler.obtainMessage(1, request);
            mRequestsHandler.sendMessage(msg);
        } else {
            request.onError(new IllegalStateException("incorrect state"));
        }
    }

    // --- public --- //

    // --- private --- //

    // Reference to the user
    private final Activity mActivity;
    private final ValitorListener mListener;

    // booleans to keep some of the state of the services.
    private boolean mStarted = false;
    private boolean mBound = false;
    // Fields needed to interact with the Ingenico devices
    private PclUtilities mPclUtilities = null;
    private PclService mPclService = null;
    // Service Connection for the Pcl Service
    final private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PclService.LocalBinder binder = (PclService.LocalBinder) service;
            mPclService = binder.getService();
            mPclService.addDynamicBridge(ValitorConfig.sPort, 1);
            mListener.onServiceConnected();

            byte[] bytes = new byte[]{0};
            if (mPclService.serverStatus(bytes) && bytes[0] == 0x10) {
                startThread();
            } else {
                mActivity.registerReceiver(mStateReceiver = new BTReceiver(),
                        new IntentFilter("com.ingenico.pclservice.intent.action.STATE_CHANGED"));
            }
        }

        // Not likely to be called
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mListener.onServiceDisconnected();
            stop();
        }
    };

    // Receiver to listen for bluetooth status changes
    private BTReceiver mStateReceiver = null;

    // Receiver to listen for bluetooth status changes
    private class BTReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra("state");
            log("state = " + String.valueOf(state));

            if (mPclService != null && state != null && state.compareTo("CONNECTED") == 0) {
                startThread();
            } else {
                Valitor.this.stop();
            }
        }
    }

    // Minimum config for the socket communication
    private static final int sAcceptTimeout = (int) TimeUnit.SECONDS.toMillis(10);
    private static final String sCharsetName = "ISO-8859-1";

    // Threads and sockets that handle the communication to the POSI device
    private Handler mRequestsHandler = null;
    private HandlerThread mHandlerThread = null;
    private ServerSocket mServerSocket = null;
    private Socket mSocket = null;

    private void startThread() {
        if (mHandlerThread != null || mPclService == null) {
            return;
        }
        mHandlerThread = new HandlerThread("Valitor") {
            @Override
            protected void onLooperPrepared() {
                super.onLooperPrepared();
                try {
                    // Create the server socket and accept a connection
                    mServerSocket = new ServerSocket(ValitorConfig.sPort);
                    mServerSocket.setSoTimeout(sAcceptTimeout);
                    mSocket = mServerSocket.accept();

                    mRequestsHandler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            // Process the request
                            final BaseRequest entity = (BaseRequest) msg.obj;

                            try {
                                mSocket.setSoTimeout(entity.getTimeout());

                                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
                                        mSocket.getOutputStream(), sCharsetName));
                                BufferedReader input = new BufferedReader(new InputStreamReader(
                                        mSocket.getInputStream(), sCharsetName));

                                entity.execute(output, input);

                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        entity.onResponse();
                                    }
                                });
                            } catch (final Exception ex) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        entity.onError(ex);
                                    }
                                });
                            }
                            return true;
                        }
                    });
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onReady();
                        }
                    });
                } catch (final IOException ex) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onError(ValitorListener.OPEN_COMMUNICATION_FAILED, ex);
                            Valitor.this.stop();
                        }
                    });
                }
            }
        };
        mHandlerThread.start();
    }

    private void stopThread() {
        closeQuietly(mSocket);
        mSocket = null;
        closeQuietly(mServerSocket);
        mServerSocket = null;
        if (mRequestsHandler != null) {
            mRequestsHandler.removeCallbacksAndMessages(null);
            mRequestsHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
            mHandlerThread = null;
        }
    }

    private Intent getPclIntent(Context context) {
        Intent intent = new Intent(context, PclService.class);
        intent.putExtra("PACKAGE_NAME", ValitorConfig.getPackageName());
        intent.putExtra("FILE_NAME", ValitorConfig.FILE_NAME);
        intent.putExtra("ENABLE_LOG", ValitorConfig.ENABLE_LOG);
        intent.putExtra("SSL_OBJECT", new SslObject("serverb.p12", "coucou"));
        return intent;
    }

    private void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
            // Empty
        }
    }

    // --- private --- //

}
