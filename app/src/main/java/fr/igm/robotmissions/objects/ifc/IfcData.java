package fr.igm.robotmissions.objects.ifc;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class IfcData implements Serializable {

    @SerializedName("floors")
    private HashMap<String, IfcFloor> floorMap;

    private IfcDimensions dimensions;

    //recuperer le plan de l'ifc
    public HashMap<String, IfcFloor> getFloorMap() {
        return floorMap;
    }

    //recuperer les dimensions de l'ifc
    public IfcDimensions getDimensions() {
        return dimensions;
    }


    //afficher les données relatives à l'ifc
    @Override
    public String toString() {
        return "IfcData{" +
                "floorMap=" + floorMap +
                ", dimensions=" + dimensions +
                '}';
    }
}
