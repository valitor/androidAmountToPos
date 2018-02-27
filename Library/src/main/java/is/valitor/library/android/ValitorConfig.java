package is.valitor.library.android;

import static is.valitor.library.android.ValitorUtils.checkStringNotEmpty;

public final class ValitorConfig {

    static public boolean ENABLE_LOG = false;
    static public String PACKAGE_NAME = null;

    static final String FILE_NAME = "pairing_addr.txt";
    static final int sPort = 9599;

    static String getPackageName() {
        return checkStringNotEmpty(PACKAGE_NAME, "missing ValitorConfig.PACKAGE_NAME = <package>");
    }

    private ValitorConfig() {

    }

}
