package fr.igm.robotmissions.objects.ifc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

import io.swagger.client.model.DeplacementMission;

public class IfcData implements Serializable {

    @SerializedName("floors")
    private HashMap<String, IfcFloor> floorMap;

    private IfcDimensions dimensions;

    public HashMap<String, IfcFloor> getFloorMap() {
        return floorMap;
    }

    public IfcDimensions getDimensions() {
        return dimensions;
    }


    @Override
    public String toString() {
        return "IfcData{" +
                "floorMap=" + floorMap +
                ", dimensions=" + dimensions +
                '}';
    }
}
