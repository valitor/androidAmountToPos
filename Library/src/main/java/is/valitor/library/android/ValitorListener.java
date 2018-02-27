package is.valitor.library.android;

import static is.valitor.library.android.Valitor.log;

public interface ValitorListener {

    String NO_ACTIVE_DEVICE = "NO_ACTIVE_DEVICE";
    String START_FAILED = "START_FAILED";
    String BIND_FAILED = "BIND_FAILED";
    String OPEN_COMMUNICATION_FAILED = "OPEN_COMMUNICATION_FAILED";
    String STOP_FAILED = "STOP_FAILED";

    void onServiceStarted();

    void onServiceBound();

    void onServiceConnected();

    void onReady();

    void onServiceDisconnected();

    void onServiceUnbound();

    void onServiceStopped();

    void onError(String error, Exception ex);

    class NOP implements ValitorListener {
        @Override
        public void onServiceStarted() {
            log("onServiceStarted");
        }

        @Override
        public void onServiceBound() {
            log("onServiceBound");
        }

        @Override
        public void onServiceConnected() {
            log("onServiceConnected");
        }

        @Override
        public void onReady() {
            log("onReady");
        }

        @Override
        public void onServiceDisconnected() {
            log("onServiceDisconnected");
        }

        @Override
        public void onServiceUnbound() {
            log("onServiceUnbound");
        }

        @Override
        public void onServiceStopped() {
            log("onServiceStopped");
        }

        @Override
        public void onError(String error, Exception ex) {
            log("Error = " + error, ex);
        }
    }

}
