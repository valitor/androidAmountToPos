package is.valitor.library.example;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ingenico.pclutilities.PclUtilities;

import java.util.ArrayList;
import java.util.List;

import static is.valitor.library.android.ValitorUtils.checkNotNull;

public class CompanionsAdapter
        extends RecyclerView.Adapter<CompanionViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final CompanionListener mListener;
    private final ArrayList<PclUtilities.BluetoothCompanion> mCompanions = new ArrayList<>();

    public CompanionsAdapter(Context context, CompanionListener listener) {
        mLayoutInflater = LayoutInflater.from(checkNotNull(context, "Context can't be null"));
        mListener = checkNotNull(listener, "CompanionListener can't be null");
    }

    @Override
    public CompanionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CompanionViewHolder(
                mLayoutInflater.inflate(R.layout.cell_companion, parent, false),
                mListener);
    }

    @Override
    public void onBindViewHolder(CompanionViewHolder holder, int position) {
        holder.bindTo(mCompanions.get(position));
    }

    @Override
    public int getItemCount() {
        return mCompanions.size();
    }

    synchronized public void replaceCompanions(List<PclUtilities.BluetoothCompanion> companions) {
        mCompanions.clear();
        mCompanions.addAll(companions);
        notifyDataSetChanged();
    }

}
