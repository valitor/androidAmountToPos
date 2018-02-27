package is.valitor.library.android;

import android.util.ArrayMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static is.valitor.library.android.Valitor.log;
import static is.valitor.library.android.ValitorUtils.checkNotNull;
import static is.valitor.library.android.ValitorUtils.checkStringNotEmpty;

/**
 * Base class for all requests to extend, it handles the base logic like creating the message
 * string request, writing and reading from the communication channel, etc...
 */
public abstract class BaseRequest {

    @ValitorProperty(name = ValitorConstants.MSG_TYPE)
    public final String mMsgType;

    public BaseRequest(MsgType type) {
        mMsgType = checkNotNull(type, "invalid type").mType;
    }

    /**
     * Callback to notifies that this request has finished.
     */
    public abstract void onResponse();

    /**
     * Callback to notify an error in the process of this request.
     *
     * @param ex The exception that caused the error.
     */
    public abstract void onError(Exception ex);

    /**
     * Call this function if you want to add any extra parameter that will be in the middle of the
     * message string request (no order is guaranteed).
     *
     * @param name  The name of the parameter.
     * @param value The value of the parameter.
     */
    public void addExtraOutput(String name, String value) {
        mOutputExtras.put(name, value);
    }

    /**
     * Retrieve an map that contains the response from the POSI device.
     *
     * @return An ArrayMap<String, String> with the POSI's response.
     */
    public ArrayMap<String, String> getInputExtras() {
        return new ArrayMap<>(mInputExtras);
    }

    protected final ArrayMap<String, String> mInputExtras = new ArrayMap<>();
    protected final ArrayMap<String, String> mOutputExtras = new ArrayMap<>();

    // --- construct --- //

    /**
     * Retrieve the timeout for this request.
     *
     * @return The timeout in millis.
     */
    public int getTimeout() {
        return (int) TimeUnit.MINUTES.toMillis(2);
    }

    /**
     * Process this request, any derived class that overrides this function and doesn't call
     * to super must make the call constructFields(getClass()); as the first statement.
     *
     * @param output The writer where any message must be written.
     * @param input  The reader where any message bust be read.
     * @throws IOException
     */
    public void execute(BufferedWriter output, BufferedReader input)
            throws IOException {
        constructFields(getClass());

        String message = toRequestString();
        send(output, message);

        String received = receive(input);
        parseReceived(received, mInputExtras);
    }

    /**
     * Function to be overridden to filter fields annotated with the annotation ValitorProperty.
     * This is helpful for when you want to reorder the parameters in the message string request.
     *
     * @param field The field containing the annotation of ValitorProperty.
     * @param name  The name of the field as it will appear in the message string request.
     * @return True if you want BaseRequest to handle the field normally, false otherwise.
     */
    protected boolean allowField(Field field, String name) {
        return name.compareToIgnoreCase(ValitorConstants.MSG_TYPE) != 0;
    }

    /**
     * Cache the fields that will be used by this request when creating the message string request.
     *
     * @param clazz The class to inspect for ValitorProperty annotated fields.
     */
    protected void constructFields(Class<? extends BaseRequest> clazz) {
        if (clazz == null || mVisited.containsKey(clazz)) {
            return;
        }
        mVisited.put(clazz, clazz);

        Field field;
        String name;
        ValitorProperty property;
        Field[] declaredFields = clazz.getDeclaredFields();
        int size = declaredFields != null ? declaredFields.length : 0;
        // Loop through all the fields and check if any has the ValitorProperty annotation,
        // and if so, try to cache it.
        for (int i = 0; i < size; ++i) {
            field = declaredFields[i];
            property = field.getAnnotation(ValitorProperty.class);
            if (property != null) {
                name = checkStringNotEmpty(property.name(), "name can't be empty");
                if (allowField(field, name)) {
                    mFields.put(name, field);
                }
            }
        }

        // Apply the same process for any super class of derived of BaseRequest.
        Class<?> sClazz = clazz.getSuperclass();
        while (sClazz != null) {
            if (BaseRequest.class.isAssignableFrom(sClazz)) {
                //noinspection unchecked
                constructFields((Class<? extends BaseRequest>) sClazz);
            }
            sClazz = sClazz.getSuperclass();
        }
    }

    private final ArrayMap<String, Field> mFields = new ArrayMap<>();
    private final ArrayMap<Class, Class> mVisited = new ArrayMap<>();

    // --- construct --- //

    // --- send --- //

    /**
     * Construct the message string request representation of this instance, by overriding some of
     * the methods we can control the order in which the fields appear.
     *
     * @return The message string request representation of this.
     */
    public String toRequestString() {
        StringBuilder builder = new StringBuilder();

        appendPreFields(builder);
        Set<Map.Entry<String, Field>> entrySet = mFields.entrySet();
        for (Map.Entry<String, Field> entry : entrySet) {
            appendField(builder, entry.getValue(), entry.getKey());
        }
        Set<Map.Entry<String, String>> extrasSet = mOutputExtras.entrySet();
        for (Map.Entry<String, String> extraOut : extrasSet) {
            appendField(builder, extraOut.getKey(), extraOut.getValue());
        }
        appendPostFields(builder);

        return builder.toString();
    }

    /**
     * Writes a message to a writer.
     *
     * @param output  The writer where the message wil be written.
     * @param message The message to be written.
     * @throws IOException If an I/O error occurs.
     */
    protected void send(BufferedWriter output, String message)
            throws IOException {
        log("Sending -> " + message);
        output.write(message);
        output.flush();
    }

    /**
     * Allow any derived class to append any field that should appear at the beginning.
     * Always call super first.
     *
     * @param builder The builder where the message is gathered.
     */
    protected void appendPreFields(StringBuilder builder) {
        appendField(builder, ValitorConstants.MSG_TYPE, mMsgType);
    }

    /**
     * Allow any derived class to append any field that should appear at the end.
     * Always call super after.
     *
     * @param builder The builder where the message is gathered.
     */
    protected void appendPostFields(StringBuilder builder) {

    }

    /**
     * Appends a field into a builder.
     *
     * @param builder The builder where to append the field.
     * @param name    The name of the field.
     * @param value   The value of the field.
     */
    protected void appendField(StringBuilder builder, String name, String value) {
        builder.append(name);
        builder.append("=");
        builder.append(value);
        builder.append("&");
    }

    /**
     * Appends a field into a builder.
     *
     * @param builder The builder where to append the field.
     * @param field   The field where the value will be extracted.
     * @param name    The name of the field.
     */
    private void appendField(StringBuilder builder, Field field, String name) {
        Object value;
        boolean accessible;

        accessible = field.isAccessible();
        try {
            value = field.get(this);
        } catch (IllegalAccessException e) {
            value = null;
        }
        field.setAccessible(accessible);

        if (value != null) {
            appendField(builder, name, String.valueOf(value));
        }
    }

    // --- send --- //

    // --- receive --- //

    /**
     * Try to parse a string into an ArrayMap<String, String>.
     *
     * @param src         The string to be parsed.
     * @param inputExtras A map where to put the values.
     */
    public void parseReceived(String src, ArrayMap<String, String> inputExtras) {
        src = checkStringNotEmpty(src, "invalid src");

        String name;
        String value;
        String[] splitField;
        String[] splitFields = src.split("&");
        int size = splitFields.length;
        for (int i = 0; i < size; ++i) {
            splitField = splitFields[i].split("=");
            if (splitField.length == 2) {
                name = checkStringNotEmpty(splitField[0], "invalid field name");
                value = splitField[1];
                inputExtras.put(name, value);
            }
        }
    }

    /**
     * Read a message from the reader.
     *
     * @param input The reader from where the message will be read.
     * @return The message read as a String.
     * @throws IOException If an I/O error occurs.
     */
    protected String receive(BufferedReader input)
            throws IOException {
        char[] buffer = new char[4096];
        int read = input.read(buffer);
        if (read == -1) {
            throw new IOException("Socket closed (read -1)");
        }
        String received = new String(buffer, 0, read);
        log("Received <- " + received);
        return received;
    }

    // --- receive --- //

}
