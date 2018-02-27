package is.valitor.library.example;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ingenico.pclutilities.PclUtilities;

import java.util.ArrayList;
import java.util.Set;

import is.valitor.library.android.Valitor;

public class CompanionPickerDialog
        extends Dialog {

    public CompanionPickerDialog(@NonNull Context context,
                                 Valitor valitor,
                                 final CompanionListener listener) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_companion_picker);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.argb((int) (255 * .25), 0, 0, 0)));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        RecyclerView recyclerView = findViewById(R.id.dialog_companion_picker_list);
        CompanionsAdapter adapter = new CompanionsAdapter(context, new CompanionListener() {
            @Override
            public void onCompanionClicked(PclUtilities.BluetoothCompanion companion) {
                dismiss();
                listener.onCompanionClicked(companion);
            }
        });
        recyclerView.setAdapter(adapter);

        Set<PclUtilities.BluetoothCompanion> devices = valitor.getDevices();
        ArrayList<PclUtilities.BluetoothCompanion> companions = new ArrayList<>();
        if (devices != null) {
            companions.addAll(devices);
        }
        if (companions.size() > 0) {
            adapter.replaceCompanions(companions);
        } else {
            ((TextView) findViewById(R.id.dialog_companion_picker_message)).
                    setText("You have no paired companions");
        }
    }

    @Override
    public void onBackPressed() {
        // Empty
    }

}
