package is.valitor.library.example;

import android.app.Application;

import is.valitor.library.android.ValitorConfig;

public class MainApplication
        extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Don't forget to do this, this is very important
        ValitorConfig.PACKAGE_NAME = getPackageName();
        // You can toggle the Library logs by setting this value
        ValitorConfig.ENABLE_LOG = true;
    }

}
