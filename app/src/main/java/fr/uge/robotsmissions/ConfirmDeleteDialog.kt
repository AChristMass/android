package fr.uge.robotsmissions

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.DialogFragment
import android.view.KeyEvent.KEYCODE_BACK



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
            .setOnKeyListener { dialog, keyCode, event -> keyCode == KeyEvent.KEYCODE_BACK }
            .setCancelable(false)
        val dialog: Dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog

        }
}

