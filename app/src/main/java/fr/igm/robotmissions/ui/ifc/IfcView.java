package fr.igm.robotmissions.ui.ifc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.ifc.IfcData;
import fr.igm.robotmissions.objects.ifc.IfcPoint;
import fr.igm.robotmissions.objects.ifc.IfcPointDeserializer;
import fr.igm.robotmissions.ui.utils.StateImageButton;
import io.swagger.client.model.Ifc;

public class IfcView extends ConstraintLayout {

    public final static Gson gson;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IfcPoint.class, new IfcPointDeserializer());
        gson = gsonBuilder.create();
    }

    private Ifc ifc;
    private final IfcDataView ifcDataView;

    private final Spinner floorSpinner;
    private final ArrayAdapter<String> floorAdapter;

    private final StateImageButton focusStartButton;
    private final StateImageButton editStartButton;

    private final StateImageButton focusEndButton;
    private final StateImageButton editEndButton;

    private final LinearLayout startLayout;
    private final LinearLayout endLayout;

    private final TextView ifcNameText;

    private IfcData ifcData;

    public IfcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ifc_view, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IfcView);
        boolean hidePosCtrl = a.getBoolean(R.styleable.IfcView_hidePositions, false);

        // get childs
        ifcDataView = findViewById(R.id.ifc_view_data);
        floorSpinner = findViewById(R.id.ifc_view_floor_spinner);
        floorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(floorAdapter);
        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modifyFloor(floorAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        focusStartButton = findViewById(R.id.ifc_view_start_focus_button);
        focusStartButton.setOnClickListener((_v)->ifcDataView.focusStart());
        editStartButton = findViewById(R.id.ifc_view_start_edit_button);
        editStartButton.setOnClickListener(
                (_v) -> {
                    ifcDataView.setOnTap((pts) -> {
                        ifcDataView.setStart(pts);
                        ifcDataView.updateView();
                        ifcDataView.setOnTap(null);
                    });
                    ifcDataView.invalidate();
                }
        );

        focusEndButton = findViewById(R.id.ifc_view_end_focus_button);
        focusEndButton.setOnClickListener((_v)->ifcDataView.focusEnd());
        editEndButton = findViewById(R.id.ifc_view_end_edit_button);
        editEndButton.setOnClickListener(
                (_v) -> {
                    ifcDataView.setOnTap((pts) -> {
                        ifcDataView.setEnd(pts);
                        ifcDataView.updateView();
                        ifcDataView.setOnTap(null);
                    });
                    ifcDataView.invalidate();
                }
        );

        startLayout = findViewById(R.id.ifc_view_start_position_layout);
        endLayout = findViewById(R.id.ifc_view_end_position_layout);

        ifcNameText = findViewById(R.id.ifc_view_name);
        if (hidePosCtrl)
            hidePositionControl();

    }

    private void modifyFloor(String floor) {
        if (!floor.equals(ifcDataView.getCurrentFloor())){
            ifcDataView.setStart(null);
            ifcDataView.setEnd(null);
            ifcDataView.setCurrentFloor(floor);
        }
        ifcDataView.resetTransformation();
        ifcDataView.updateView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setIfc(Ifc ifc) {
        if (ifc.equals(this.ifc)) {
            ifcDataView.invalidate();
            return;
        }
        this.ifc = ifc;

        resetPositions();
        floorAdapter.clear();

        ifcData = gson.fromJson(ifc.getData(), IfcData.class);
        ifcDataView.setIfcData(ifcData);
        floorAdapter.addAll(ifcData.getFloorMap().keySet());
        ifcNameText.setText(String.format(getResources().getString(R.string.ifc_view_name), ifc.getName()));
    }

    public void setPositions(String floor, float sX, float sY, float eX, float eY) {
        modifyFloor(floor);
        for (int i = 0; i < floorAdapter.getCount(); i++) {
            if (floor.equals(floorAdapter.getItem(i))) {
                floorSpinner.setSelection(i);
                break;
            }
        }
        ifcDataView.setStart(new float[]{sX, sY});
        ifcDataView.setEnd(new float[]{eX, eY});
        ifcDataView.updateView();
    }

    private void resetPositions() {
        ifcDataView.setStart(null);
        ifcDataView.setEnd(null);
        ifcDataView.updateView();
    }

    private void hidePositionControl() {
        startLayout.setVisibility(View.GONE);
        endLayout.setVisibility(View.GONE);
    }

    public void setPositionEdit(boolean state){
        editEndButton.setEnabled(state);
        editStartButton.setEnabled(state);
        floorSpinner.setEnabled(state);
        ifcDataView.setOnTap(null);
    }

    public Ifc getIfc() {
        return ifc;
    }

    public String getFloor() {
        return ifcDataView.getCurrentFloor();
    }

    public float[] getStartPosition() {
        return ifcDataView.getStart();
    }

    public float[] getEndPosition() {
        return ifcDataView.getEnd();
    }

    public void setRobotPosition(int[] robotPos) {
        ifcDataView.setRobot(robotPos);
        ifcDataView.updateView();
    }
}
