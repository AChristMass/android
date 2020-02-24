package fr.uge.robotsmissions.ui.robots

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
import fr.uge.robotsmissions.objects.Robot
import kotlinx.android.synthetic.main.fragment_robots.*
import kotlinx.android.synthetic.main.fragment_robots.view.*
import kotlinx.android.synthetic.main.fragment_robots.view.robot_floatingActionButton


class RobotsFragment : Fragment(), View.OnClickListener {
    internal var root: View? = null
    lateinit var adapter: RobotAdapter
    private lateinit var robotsViewModel: RobotsViewModel

    var robots_array = arrayListOf(
        Robot("Robot 1", "", false, 100F),
        Robot("Robot 2", "a", true, 100F),
        Robot("Robot 3", "a", true, 100F),
        Robot("Robot 4", "a", true, 100F),
        Robot("Robot 5", "a", true, 100F),
        Robot("Robot 6", "a", true, 100F),
        Robot("Robot 7", "a", true, 100F),
        Robot("Robot 8", "a", true, 100F),
        Robot("Robot 9", "a", true, 100F),
        Robot("Robot 10", "a", true, 100F),
        Robot("Robot 11", "a", true, 100F),
        Robot("Robot 12", "a", true, 100F),
        Robot("Robot 13", "a", true, 100F),
        Robot("Robot 14", "a", true, 100F),
        Robot("Robot 15", "a", true, 100F),
        Robot("Robot 16", "a", true, 100F),
        Robot("Robot 17", "a", true, 100F),
        Robot("Robot 18", "a", true, 100F),
        Robot("Robot 19", "a", true, 100F),
        Robot("Robot 20", "a", true, 100F),
        Robot("Robot 21", "a", true, 100F),
        Robot("Robot 22", "a", true, 100F),
        Robot("Robot 23", "a", true, 100F)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        robotsViewModel =
            ViewModelProviders.of(this).get(RobotsViewModel::class.java)
        root = inflater.inflate(fr.uge.robotsmissions.R.layout.fragment_robots, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RobotAdapter(robots_array, this)
        root!!.robots_list.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        root!!.robots_list.adapter = adapter

        val swipeHandler = object : SwipeDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showConfirmDialog(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(root!!.robots_list)

        robot_floatingActionButton.setOnClickListener(this)
    }

    private fun showConfirmDialog(position: Int) {
        val confirmFragment = ConfirmDeleteDialog(robots_array[position].name)
        confirmFragment.listener = object : ConfirmDeleteDialog.ConfirmDeleteDialogListener {
            override fun onDialogNegativeClick() {
                adapter.notifyDataSetChanged()
            }

            override fun onDialogPositiveClick() {
                robots_array.removeAt(position)
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
                R.id.robot_floatingActionButton -> println("Button : Robot")
            }
        }
    }

    fun showDetails(index: Int) {
        val mission = robots_array[index]
        val intent = Intent(root?.context, RobotDetails::class.java)
        intent.putExtra(RobotDetails.EXTRA_ROBOT, mission as Parcelable)
        startActivity(intent)
    }

}