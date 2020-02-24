package fr.uge.robotsmissions.ui.ifc

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
import fr.uge.robotsmissions.objects.Ifc
import kotlinx.android.synthetic.main.fragment_ifc.view.*
import java.text.SimpleDateFormat
import java.util.*

class IfcFragment : Fragment(), View.OnClickListener {

    private lateinit var ifcViewModel: IfcViewModel
    internal var root: View? = null
    lateinit var adapter: IfcAdapter
    val pattern: String = "dd MM yyyy HH:mm:ss"
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(pattern)
    var ifc_array = arrayListOf(
        Ifc("IFC 1", Date(2020 - 1900, 3, 12)),
        Ifc("IFC 2", Date(2019 - 1900, 5, 22))
    ) //Date starts from 1900, need to substract 1900 from it to be accurate. Months starts from 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ifcViewModel =
            ViewModelProviders.of(this).get(IfcViewModel::class.java)

        root = inflater.inflate(R.layout.fragment_ifc, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = IfcAdapter(ifc_array, this)
        root!!.ifc_list.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        root!!.ifc_list.adapter = adapter

        val swipeHandler = object : SwipeDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showConfirmDialog(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(root!!.ifc_list)
    }

    private fun showConfirmDialog(position: Int) {
        val confirmFragment = ConfirmDeleteDialog(ifc_array[position].name)
        confirmFragment.listener = object : ConfirmDeleteDialog.ConfirmDeleteDialogListener {
            override fun onDialogNegativeClick() {
                adapter.notifyDataSetChanged()
            }

            override fun onDialogPositiveClick() {
                ifc_array.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        }
        confirmFragment.show(fragmentManager, "confirmDeleteDialog")
    }

    override fun onClick(v: View) {
        if(v.tag != null)
            showDetails(v.tag as Int)
    }


    fun showDetails(index: Int){
        val ifc = ifc_array[index]
        val intent = Intent(root?.context, IfcDetails::class.java)
        intent.putExtra(IfcDetails.EXTRA_IFC, ifc as Parcelable)
        startActivity(intent)

    }
}