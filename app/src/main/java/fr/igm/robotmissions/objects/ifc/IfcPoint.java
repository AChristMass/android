package fr.igm.robotmissions.objects.ifc;

import java.io.Serializable;

public class IfcPoint implements Serializable {

    private float x;
    private float y;

    public IfcPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
