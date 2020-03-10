package fr.igm.robotmissions.objects.ifc;


import android.graphics.Point;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class IfcFloor implements Serializable {

    @SerializedName("spacesInfos")
    private Map<String, IfcSpaceInfo> spacesInfos;

    @SerializedName("spacesPolygons")
    private Map<String, ArrayList<IfcPoint>> spacesPolygons;

    @SerializedName("doorsPolygons")
    private Map<String, ArrayList<IfcPoint>> doorsPolygons;

    public Map<String, ArrayList<IfcPoint>> getDoorsPolygons() {
        return doorsPolygons;
    }

    public Map<String, ArrayList<IfcPoint>> getSpacesPolygons() {
        return spacesPolygons;
    }

    public Map<String, IfcSpaceInfo> getSpacesInfos() {
        return spacesInfos;
    }


    @Override
    public String toString() {
        return "IfcFloor{" +
                "spacesInfos=" + spacesInfos +
                ", spacesPolygons=" + spacesPolygons +
                ", doorsPolygons=" + doorsPolygons +
                '}';
    }
}
