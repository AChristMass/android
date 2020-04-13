package fr.igm.robotmissions.ui.ifc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.utils.Utils;
import io.swagger.client.model.Ifc;

public class IfcAdapter extends RecyclerView.Adapter<IfcAdapter.ViewHolder> {

    private List<Ifc> ifcList = new ArrayList<>();
    private IfcFragment fragment;

    IfcAdapter(IfcFragment fragment) {
        super();
        this.fragment = fragment;
    }

    void setIfcList(List<Ifc> ifcList) {
        this.ifcList = ifcList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ifc, parent, false);
        return new IfcAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ifc ifc = ifcList.get(position);

        // set item text
        holder.nameView.setText(ifc.getName());
        holder.lastUploadView.setText(Utils.formatDateString(ifc.getLastUpload()));
        // set detail intent click
        Context context = holder.cardView.getContext();
        Intent intent = new Intent(context, IfcDetailsActivity.class);
        intent.putExtra(IfcDetailsActivity.EXTRA_IFC, ifc);

        holder.cardView.setOnClickListener((_v) -> {
            fragment.startActivityForResult(intent, IfcFragment.REQUEST_CODE);
        });

    }

    @Override
    public int getItemCount() {
        return ifcList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView nameView;
        TextView lastUploadView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.ifc_card_view);
            nameView = cardView.findViewById(R.id.ifc_name_text);
            lastUploadView = cardView.findViewById(R.id.ifc_last_upload_text);
        }
    }


}
