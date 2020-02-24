package fr.uge.robotsmissions

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmDeleteDialog(val title: String = "") : DialogFragment() {

    interface ConfirmDeleteDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var listener: ConfirmDeleteDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Delete \"$title\" ?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                listener?.onDialogPositiveClick()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                listener?.onDialogNegativeClick()
            })
        return builder.create()
    }
}