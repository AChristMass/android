package fr.uge.robotsmissions.ui.missions

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.robotsmissions.ConfirmDeleteDialog
import fr.uge.robotsmissions.R
import fr.uge.robotsmissions.SwipeDeleteCallback
import fr.uge.robotsmissions.objects.Mission
import kotlinx.android.synthetic.main.fragment_missions.*
import kotlinx.android.synthetic.main.fragment_missions.view.*
import kotlinx.android.synthetic.main.fragment_robots.*
import java.util.*

class MissionsFragment : Fragment(), View.OnClickListener {


    internal var root: View? = null
    lateinit var adapter: MissionAdapter
    private lateinit var missionsViewModel: MissionsViewModel

    var missions_array = arrayListOf(
        Mission("Mission 1", "IFC", Date(2020 - 1900, 2, 12)),
        Mission("Mission 2", "IFC", Date(2019 - 1900, 11, 22))
    ) //Date starts from 1900, need to substract 1900 from it to be accurate. Months starts from 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        missionsViewModel =
            ViewModelProviders.of(this).get(MissionsViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_missions, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MissionAdapter(missions_array, this)
        root!!.missions_list.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        root!!.missions_list.adapter = adapter


        val swipeHandler = object : SwipeDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showConfirmDialog(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(root!!.missions_list)

        mission_search_button.setOnClickListener(this)
        mission_floatingActionButton.setOnClickListener(this)
    }

    private fun showConfirmDialog(position: Int) {
        val confirmFragment = ConfirmDeleteDialog(missions_array[position].name)
        confirmFragment.listener = object : ConfirmDeleteDialog.ConfirmDeleteDialogListener {
            override fun onDialogNegativeClick() {
                adapter.notifyDataSetChanged()
            }

            override fun onDialogPositiveClick() {
                missions_array.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        }
        confirmFragment.show(fragmentManager, "confirmDeleteDialog")
    }

    override fun onClick(v: View) {
        if (v.tag != null) {
            showDetails(v.tag as Int)
        }
        else {
            when (v.id) {
                R.id.mission_floatingActionButton -> println("Button : Robot")
                R.id.mission_search_button -> executeSearch()
            }
        }
    }

    private fun executeSearch() {
        println(mission_search_text.text)
    }


    fun showDetails(index: Int) {
        val mission = missions_array[index]
        val intent = Intent(root?.context, MissionDetails::class.java)
        intent.putExtra(MissionDetails.EXTRA_MISSION, mission as Parcelable)
        startActivity(intent)
    }

}