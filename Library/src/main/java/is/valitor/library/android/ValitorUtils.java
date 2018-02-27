package is.valitor.library.android;

public final class ValitorUtils {

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    static public <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will
     *                     be converted to a string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    static public <T> T checkNotNull(final T reference, final Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    static public String checkStringNotEmpty(final String reference) {
        checkNotNull(reference);
        String trim = reference.trim();
        if (trim.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return trim;
    }

    static public String checkStringNotEmpty(final String reference, final Object errorMessage) {
        checkNotNull(reference, errorMessage);
        String trim = reference.trim();
        if (trim.isEmpty()) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return trim;
    }

    static public String valitorAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount < 0");
        }
        return String.valueOf(amount * 100);
    }

    private ValitorUtils() {

    }

}
