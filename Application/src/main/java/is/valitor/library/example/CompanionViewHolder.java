package is.valitor.library.example;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ingenico.pclutilities.PclUtilities;

import static is.valitor.library.android.ValitorUtils.checkNotNull;

public class CompanionViewHolder
        extends RecyclerView.ViewHolder implements View.OnClickListener {

    private PclUtilities.BluetoothCompanion mCompanion = null;
    private final CompanionListener mListener;
    private final TextView mTextView;

    public CompanionViewHolder(View itemView, CompanionListener listener) {
        super(itemView);
        mListener = checkNotNull(listener, "CompanionListener can't be null");
        mTextView = itemView.findViewById(R.id.cell_companion_name);
        mTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mListener.onCompanionClicked(mCompanion);
    }

    public void bindTo(PclUtilities.BluetoothCompanion companion) {
        mCompanion = checkNotNull(companion, "Can't bind to a null companion");
        mTextView.setText(companion.getName());
    }

}
