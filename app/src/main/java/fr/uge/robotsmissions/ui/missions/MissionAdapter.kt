package fr.uge.robotsmissions.ui.missions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.uge.robotsmissions.R
import fr.uge.robotsmissions.objects.Mission
import kotlinx.android.synthetic.main.item_mission.view.*
import java.text.SimpleDateFormat


class MissionAdapter(private val missions: ArrayList<Mission>, val itemClickListener: View.OnClickListener) :
    RecyclerView.Adapter<MissionAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val cardView = itemView.mission_card_view!!
        val nameView = cardView.mission_name_text!!
        val ifcView = cardView.mission_ifc_target_name_text!!
        var launchedView = cardView.mission_last_launched_text!!


        val clickListener = itemView.setOnClickListener(this)

        override fun onClick(v: View?) {
            var position: Int = layoutPosition
            println(position)
        }
    }

    override fun getItemCount(): Int {
        return missions.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionAdapter.ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mission, parent, false)
        return MissionAdapter.ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: MissionAdapter.ViewHolder, position: Int) {
        val mission = missions[position]
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY")
        holder.cardView.setOnClickListener(itemClickListener)

        holder.cardView.tag = position
        holder.nameView.text = mission.name
        holder.ifcView.text = "IFC Target : ${mission.ifc_target_name}"
        holder.launchedView.text =
            "Last launched : ${simpleDateFormat.format(mission.last_launched)}"

    }

    fun removeAt(position: Int) {
        missions.removeAt(position)
        notifyItemRemoved(position)
    }

    fun isEnabled(position: Int): Boolean {
        return true
    }

}

