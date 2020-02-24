package fr.uge.robotsmissions.ui.missions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.uge.robotsmissions.R
import fr.uge.robotsmissions.objects.Mission
import kotlinx.android.synthetic.main.activity_mission_details.*
import kotlinx.android.synthetic.main.item_mission.*
import java.text.SimpleDateFormat

class MissionDetails : AppCompatActivity() {

    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY")

    companion object {
        val EXTRA_MISSION = "mission"
    }
    lateinit var mission: Mission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission_details)

        mission = intent.getParcelableExtra(EXTRA_MISSION)
        supportActionBar?.hide()
        mission_toolbar.setTitle(mission.name)
        mission_ifc_target.text = "IFC Target : ${mission.ifc_target_name}"
        mission_last_launched.text = "Last launched : ${simpleDateFormat.format(mission.last_launched)}"
    }
}
