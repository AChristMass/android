package fr.igm.robotmissions.ui.missions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.utils.SimpleApiCallback;
import fr.igm.robotmissions.objects.utils.Utils;
import fr.igm.robotmissions.ui.ifc.IfcView;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.api.IfcApi;
import io.swagger.client.api.MissionApi;
import io.swagger.client.model.DeplacementMission;
import io.swagger.client.model.Ifc;

public class MissionDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MISSION = "MISSIONEXTRA";
    private static final String EXTRA_HAS_CHANGE = "MISSIONHASCHANGEEXTRA";
    private Spinner ifcSpinner;
    private ArrayAdapter<String> ifcSpinnerAdapter;
    private IfcApi ifcApi = new IfcApi();
    private MissionApi missionApi = new MissionApi();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private IfcView ifcView;
    private DeplacementMission mission;
    private EditText nameEditText;
    private TextView nameText;
    private FloatingActionButton editButton;
    private FloatingActionButton saveButton;
    private boolean isEditing;
    private boolean isCreating;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_details);

        mission = (DeplacementMission) getIntent().getSerializableExtra(EXTRA_MISSION);
        ifcView = findViewById(R.id.mission_ifc_data_view);


        nameEditText = findViewById(R.id.mission_name_edit_text);
        nameText = findViewById(R.id.mission_name_detail_text);

        editButton = findViewById(R.id.mission_edit_button);
        saveButton = findViewById(R.id.mission_save_button);
        saveButton.setOnClickListener((_v) -> {
            try {
                saveMission();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });

        ifcSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item);
        ifcSpinner = findViewById(R.id.mission_ifc_name_spinner);
        ifcSpinner.setAdapter(ifcSpinnerAdapter);

        try {
            ifcApi.listIfcAsync(new SimpleApiCallback<List<Ifc>>() {
                @Override
                public void onSuccess(List<Ifc> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    mainHandler.post(() -> {
                        List<String> ifcNames = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            Ifc ifc = result.get(i);
                            ifcNames.add(ifc.getName());
                            if (!isCreating && mission.getIfc().getId().equals(ifc.getId())) {
                                ifcSpinner.setSelection(i);
                            }
                        }
                        ifcSpinnerAdapter.addAll(ifcNames);
                        ifcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Ifc ifc = result.get(position);
                                ifcView.setIfc(ifc);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        if (isCreating){
                            ifcSpinner.setSelection(0);
                            ifcView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }

        isEditing = false;
        isCreating = mission == null;
        if (isCreating) {
            editMission();
            ifcView.setVisibility(View.GONE);
            return;
        }
        ifcSpinner.setEnabled(false);
        ifcView.setIfc(mission.getIfc());
        ifcView.setPositions(mission.getFloor(),
                mission.getStartX(),
                mission.getStartY(),
                mission.getEndX(),
                mission.getEndY());
        nameText.setText(mission.getName());
        ifcView.setPositionEdit(false);
    }

    private void saveMission() throws ApiException {
        ApiCallback<DeplacementMission> callback = new SimpleApiCallback<DeplacementMission>() {
            @Override
            public void onSuccess(DeplacementMission result, int statusCode, Map<String, List<String>> responseHeaders) {
                mainHandler.post(()-> {
                   setResult(Activity.RESULT_OK); // we have created or modified
                   finish();
                   Intent intent = getIntent();
                   intent.putExtra(EXTRA_MISSION, result);
                   startActivity(intent);
                });
            }
        };
        String name = nameEditText.getText().toString();
        if (name.isEmpty()){
            Toast.makeText(this,
                    getResources().getString(R.string.name_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int ifcId = ifcView.getIfc().getId();
        String floor = ifcView.getFloor();
        int [] start = Utils.floatArrayToIntArray(ifcView.getStartPosition());
        int [] end = Utils.floatArrayToIntArray(ifcView.getEndPosition());
        if (start == null || end == null) {
            Toast.makeText(this, getResources().getString(R.string.please_select_position), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCreating){
            missionApi.postMissionAsync(name, ifcId, floor, start[0], start[1], end[0], end[1], callback);
        }else {
            missionApi.updateMissionAsync(mission.getId(), name, ifcId, floor, start[0], start[1], end[0], end[1], callback);
        }
    }


    public void floatingButtonClick(View view) {
        if (isEditing) {
            stopEditing();
        } else {
            editMission();
        }
    }

    private void stopEditing() {
        isEditing = false;
        editButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_24dp));
        saveButton.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);
        nameText.setVisibility(View.VISIBLE);
        for (int i = 0; i < ifcSpinnerAdapter.getCount(); i++) {
            if (mission.getIfc().getName().equals(ifcSpinnerAdapter.getItem(i))) {
                ifcSpinner.setSelection(i);
                ifcView.setPositions(mission.getFloor(),
                        mission.getStartX(),
                        mission.getStartY(),
                        mission.getEndX(),
                        mission.getEndY());
                break;
            }
        }
        ifcSpinner.setEnabled(false);
        ifcView.setPositionEdit(false);
    }

    private void editMission() {
        isEditing = true;
        editButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp));
        saveButton.setVisibility(View.VISIBLE);
        nameEditText.setText(nameText.getText());
        nameEditText.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.GONE);
        ifcSpinner.setEnabled(true);
        ifcView.setPositionEdit(true);
    }

}
