package fr.uge.robotsmissions.ui.robots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.uge.robotsmissions.R
import fr.uge.robotsmissions.objects.Robot
import kotlinx.android.synthetic.main.item_robot.view.*

class RobotAdapter(private val robots: ArrayList<Robot>, val itemClickListener: View.OnClickListener) :
    RecyclerView.Adapter<RobotAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val cardView = itemView.robot_card_view!!
        val nameView = cardView.robot_name_text!!
        val positionView = cardView.robot_ifc_position_name_text!!
        var onlineView = cardView.robot_is_online_text!!
        var batteryView = cardView.robot_battery_text!!

        val clickListener = itemView.setOnClickListener(this)

        override fun onClick(v: View?) {
            var position: Int = layoutPosition
            println(position)
        }

    }

    override fun getItemCount(): Int {
        return robots.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RobotAdapter.ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_robot, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: RobotAdapter.ViewHolder, position: Int) {
        val robot = robots[position]
        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.tag = position
        holder.nameView.text = robot.name
        holder.positionView.text = "Position : ${robot.ifc_position_name}"
        holder.onlineView.text = when (robot.isOnline) {
            true -> "Status : Online"
            false -> "Status : Offline"
        }
        holder.batteryView.text = "Battery : ${robot.battery} %"

    }

    fun removeAt(position: Int) {
        robots.removeAt(position)
        notifyItemRemoved(position)
    }
}