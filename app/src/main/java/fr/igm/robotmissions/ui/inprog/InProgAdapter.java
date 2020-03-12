package fr.igm.robotmissions.ui.inprog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.utils.Utils;
import io.swagger.client.model.MissionInProg;

public class InProgAdapter extends RecyclerView.Adapter<InProgAdapter.ViewHolder> {

    private List<MissionInProg> missionInProgList = new ArrayList<>();
    private InProgFragment fragment;

    InProgAdapter(InProgFragment fragment) {
        super();
        this.fragment = fragment;
    }

    void setMissionInProgList(List<MissionInProg> ifcList) {
        this.missionInProgList = ifcList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView missionNameView;
        TextView robotNameView;
        TextView startedTextView;
        ImageView stateImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            stateImageView = itemView.findViewById(R.id.inprog_state_image);
            cardView = itemView.findViewById(R.id.inprog_card_view);
            missionNameView = itemView.findViewById(R.id.inprog_mission_name);
            robotNameView = itemView.findViewById(R.id.inprog_robot_name);
            startedTextView = itemView.findViewById(R.id.inprog_started_text);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inprog, parent, false);
        return new InProgAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MissionInProg mIP = missionInProgList.get(position);

        // set item text
        holder.missionNameView.setText(mIP.getMission().getName());
        holder.robotNameView.setText(mIP.getRobot().getName());
        holder.startedTextView.setText( Utils.formatDateString(mIP.getStartedAt()));
        if (mIP.isIsDone()) {
            holder.stateImageView.setImageResource(R.drawable.ic_done_black_24dp);
        }
        // set detail intent click
        Context context = holder.cardView.getContext();
        Intent intent = new Intent(context, InProgDetailsActivity.class);
        intent.putExtra(InProgDetailsActivity.EXTRA_INPROG, mIP);

        holder.cardView.setOnClickListener((_v) -> {
            fragment.startActivityForResult(intent, InProgFragment.REQUEST_CODE);
        });

    }

    @Override
    public int getItemCount() {
        return missionInProgList.size();
    }


}
