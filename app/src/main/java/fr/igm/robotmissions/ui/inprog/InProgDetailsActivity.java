package fr.igm.robotmissions.ui.inprog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.missions.JsonMissionMsgListener;
import fr.igm.robotmissions.objects.missions.MissionNotifHandler;
import fr.igm.robotmissions.objects.missions.NotifService;
import fr.igm.robotmissions.objects.utils.SimpleApiCallback;
import fr.igm.robotmissions.ui.ifc.IfcView;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.api.MissionApi;
import io.swagger.client.api.RobotApi;
import io.swagger.client.model.DeplacementMission;
import io.swagger.client.model.MissionInProg;
import io.swagger.client.model.Robot;

public class InProgDetailsActivity extends AppCompatActivity implements JsonMissionMsgListener {

    public static final String EXTRA_INPROG = "INPROGEXTRA";

    private MissionApi missionApi = new MissionApi();
    private RobotApi robotApi = new RobotApi();

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private IfcView ifcView;

    private TextView robotNameText;
    private Spinner robotSpinner;
    private ArrayAdapter<String> robotSpinnerAdapter;

    private TextView missionNameText;
    private Spinner missionSpinner;
    private ArrayAdapter<String> missionSpinnerAdapter;

    private MissionInProg missionInProg;

    private Robot robotSelected;
    private DeplacementMission missionSelected;

    private NotifService notifService;
    private MissionNotifHandler missionEventHandler;
    private boolean isBound;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inprog_details);

        missionInProg = (MissionInProg) getIntent().getSerializableExtra(EXTRA_INPROG);
        ifcView = findViewById(R.id.inprog_ifc_data_view);
        ifcView.setPositionEdit(false);
        robotNameText = findViewById(R.id.inprog_robot_name_text);
        robotSpinner = findViewById(R.id.inprog_robot_spinner);

        missionNameText = findViewById(R.id.inprog_mission_name_text);
        missionSpinner = findViewById(R.id.inprog_mission_spinner);

        FloatingActionButton runButton = findViewById(R.id.inprog_run_button);

        robotSpinnerAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item);
        robotSpinner.setAdapter(robotSpinnerAdapter);

        missionSpinnerAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item);
        missionSpinner.setAdapter(missionSpinnerAdapter);

        if (missionInProg != null) {
            // mission is running or done
            robotSpinner.setVisibility(View.GONE);
            missionSpinner.setVisibility(View.GONE);
            runButton.setVisibility(View.GONE);
            setMission(missionInProg.getMission());
            robotNameText.setText(missionInProg.getRobot().getName());
            missionNameText.setText(missionInProg.getMission().getName());
            if (!missionInProg.isIsDone()) {
                startMissionNotifService();
                ifcView.setRobotPosition(new int[]{missionInProg.getX(), missionInProg.getY()});
            }
            return;
        }

        runButton.setOnClickListener((_v) -> {
            try {
                runMission();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });
        // mission is not run, we are creating it
        robotSpinner.setVisibility(View.VISIBLE);
        missionSpinner.setVisibility(View.VISIBLE);
        robotNameText.setVisibility(View.GONE);
        missionNameText.setVisibility(View.GONE);
        ifcView.setVisibility(View.GONE);
        try {
            robotApi.listRobotAvailableAsync(new SimpleApiCallback<List<Robot>>() {
                @Override
                public void onSuccess(List<Robot> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    mainHandler.post(() -> {
                        if (result.isEmpty()) {
                            Toast.makeText(InProgDetailsActivity.this,
                                    getResources().getString(R.string.no_robot),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        robotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                robotSelected = result.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        List<String> robotNames = new ArrayList<>();
                        for (Robot r : result)
                            robotNames.add(r.getName());
                        robotSpinnerAdapter.addAll(robotNames);

                    });
                }
            });

            missionApi.listMissionAsync(new SimpleApiCallback<List<DeplacementMission>>() {
                @Override
                public void onSuccess(List<DeplacementMission> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    mainHandler.post(() -> {
                        if (result.isEmpty()) {
                            Toast.makeText(InProgDetailsActivity.this,
                                    getResources().getString(R.string.no_missions),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        missionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                missionSelected = result.get(position);
                                setMission(missionSelected);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        List<String> missionNames = new ArrayList<>();
                        for (DeplacementMission dm : result)
                            missionNames.add(dm.getName());
                        missionSpinnerAdapter.addAll(missionNames);

                    });
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    private void startMissionNotifService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                notifService = ((NotifService.LocalBinder) iBinder).getInstance();
                notifService.setMissionHandler(missionEventHandler);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                notifService = null;
            }
        };
        missionEventHandler = new MissionNotifHandler(this);
        Intent intent = new Intent(this, NotifService.class);
        intent.setAction(NotifService.START_FOLLOWING_MISSION);
        intent.putExtra(NotifService.EXTRA_MISSION_ID, missionInProg.getId());
        startService(intent);
        bindService(
                new Intent(this, NotifService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService() {
        if (isBound) {
            // Detach our existing connection.
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    private void setMission(DeplacementMission missionSelected) {
        ifcView.setVisibility(View.VISIBLE);
        ifcView.setIfc(missionSelected.getIfc());
        ifcView.setPositions(missionSelected.getFloor(),
                missionSelected.getStartX(),
                missionSelected.getStartY(),
                missionSelected.getEndX(),
                missionSelected.getEndY());
    }

    private void runMission() throws ApiException {
        ApiCallback<MissionInProg> callback = new SimpleApiCallback<MissionInProg>() {
            @Override
            public void onSuccess(MissionInProg result, int statusCode, Map<String, List<String>> responseHeaders) {
                mainHandler.post(() -> {
                    setResult(Activity.RESULT_OK); // we have created or modified
                    finish();
                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_INPROG, result);
                    startActivity(intent);
                });
            }
        };
        missionApi.startMissionAsync(robotSelected.getUuid(), missionSelected.getId(), callback);
    }


    @Override
    public void onReceiveMissionMsg(JSONObject msg) throws JSONException {
        if (msg.has("isDone")) {
            // mission is done
            Toast.makeText(this, "Mission done", Toast.LENGTH_SHORT).show();
            finish();
        } else if (msg.has("position")) {
            // robot position updated
            Log.i("received", "new position");
            JSONObject pos = msg.getJSONObject("position");
            int[] robotPos = new int[]{pos.getInt("x"), pos.getInt("y")};
            ifcView.setRobotPosition(robotPos);
        }
    }
}
