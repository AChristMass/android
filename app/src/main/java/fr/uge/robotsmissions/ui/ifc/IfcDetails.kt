package fr.uge.robotsmissions.ui.ifc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.uge.robotsmissions.R
import fr.uge.robotsmissions.objects.Ifc
import kotlinx.android.synthetic.main.activity_ifc_details.*
import java.text.SimpleDateFormat

class IfcDetails : AppCompatActivity() {

    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY")

    companion object {
        val EXTRA_IFC = "ifc"
    }

    lateinit var ifc: Ifc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ifc_details)

        ifc = intent.getParcelableExtra(EXTRA_IFC)
        supportActionBar?.hide()
        ifc_toolbar.setTitle(ifc.name)
        ifc_last_upload.text = "Last launched : ${simpleDateFormat.format(ifc.last_upload)}"

    }
}
