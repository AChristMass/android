package fr.igm.robotmissions.ui.missions;

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
import io.swagger.client.model.DeplacementMission;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {

    private List<DeplacementMission> missionList = new ArrayList<>();
    private MissionsFragment fragment;

    public MissionAdapter(MissionsFragment fragment) {
        super();
        this.fragment = fragment;
    }

    public void setMissionList(List<DeplacementMission> missionList) {
        this.missionList = missionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mission, parent, false);
        return new MissionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeplacementMission mission = missionList.get(position);

        // set item text
        holder.nameView.setText(mission.getName());
        holder.ifcNameView.setText(mission.getIfc().getName());
        holder.ifcFloorView.setText(mission.getFloor());
        // set detail intent click
        Context context = holder.cardView.getContext();
        Intent intent = new Intent(context, MissionDetailsActivity.class);
        intent.putExtra(MissionDetailsActivity.EXTRA_MISSION, mission);

        holder.cardView.setOnClickListener((_v) -> {
            fragment.startActivityForResult(intent, MissionsFragment.REQUEST_CODE);
        });
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ifcFloorView;
        private final View cardView;
        private final TextView nameView;
        private final TextView ifcNameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.mission_card_view);
            nameView = cardView.findViewById(R.id.mission_name_text);
            ifcNameView = cardView.findViewById(R.id.mission_ifc_target_name_text);
            ifcFloorView = cardView.findViewById(R.id.mission_ifc_floor);
        }
    }


}
