package fr.igm.robotmissions.ui.robots;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.igm.robotmissions.R;
import io.swagger.client.model.Robot;

public class RobotAdapter extends RecyclerView.Adapter<RobotAdapter.ViewHolder> {

    private List<Robot> robotList = new ArrayList<>();

    public RobotAdapter() {
        super();
    }

    public void setRobotList(List<Robot> robotList) {
        this.robotList = robotList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View cardView;
        TextView nameView;
        TextView batteryView;
        TextView isOnlineView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.robot_card_view);
            nameView = cardView.findViewById(R.id.robot_name_text);
            isOnlineView = cardView.findViewById(R.id.robot_is_online_text);
            batteryView = cardView.findViewById(R.id.robot_battery_text);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_robot, parent, false);
        return new RobotAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Robot robot = robotList.get(position);

        // set detail intent click
        Context context = holder.cardView.getContext();


        // set item text
        holder.nameView.setText(robot.getName());
        Resources res = context.getResources();
        int onlineStrId = robot.isConnected() ? R.string.is_online : R.string.is_offline;
        holder.isOnlineView.setText(res.getString(onlineStrId));
        if (robot.getStatus() != null){
            holder.batteryView.setText(
                    String.format(
                            res.getString(R.string.robot_battery),
                            robot.getStatus().getBattery())
            );
        } else {
            holder.batteryView.setText(res.getString(R.string.robot_no_status));
        }
    }

    @Override
    public int getItemCount() {
        return robotList.size();
    }


}
