package fr.uge.robotsmissions.ui.ifc

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import fr.uge.robotsmissions.R
import fr.uge.robotsmissions.objects.Ifc
import kotlinx.android.synthetic.main.item_ifc.view.*
import java.text.SimpleDateFormat

class IfcAdapter(private val ifcs: ArrayList<Ifc>, private val itemClickListener: View.OnClickListener) : RecyclerView.Adapter<IfcAdapter.ViewHolder>() {

    lateinit var context: Context;


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.ifc_card_view!!
        val nameView = cardView.ifc_name_text!!
        var lastUploadView = cardView.ifc_last_upload_text!!


    }

    override fun getItemCount(): Int {
        return ifcs.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IfcAdapter.ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ifc, parent, false)
        return IfcAdapter.ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: IfcAdapter.ViewHolder, position: Int) {
        val ifc = ifcs[position]
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY")
        holder.cardView.setOnClickListener(itemClickListener)
        holder.cardView.tag = position
        holder.nameView.text = ifc.name
        holder.lastUploadView.text = "Last Upload : ${simpleDateFormat.format(ifc.last_upload)}"
    }

    fun removeAt(position: Int) {
        ifcs.removeAt(position)
        notifyItemRemoved(position)
    }



}