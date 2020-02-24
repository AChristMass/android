package fr.uge.robotsmissions.ui.robots

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.uge.robotsmissions.R
import fr.uge.robotsmissions.objects.Robot
import kotlinx.android.synthetic.main.activity_robot_details.*
import java.text.SimpleDateFormat

class RobotDetails : AppCompatActivity() {
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY")

    companion object {
        val EXTRA_ROBOT = "robot"
    }
    lateinit var robot: Robot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_details)

        robot = intent.getParcelableExtra(EXTRA_ROBOT)
        supportActionBar?.hide()
        robot_toolbar.setTitle(robot.name)
        robot_position.text = "Position : ${robot.ifc_position_name}"
        robot_status.text = when (robot.isOnline) {
            true -> "Status : Online"
            false -> "Status : Offline"
        }
        robot_battery.text = "Battery : ${robot.battery}"
    }
}
