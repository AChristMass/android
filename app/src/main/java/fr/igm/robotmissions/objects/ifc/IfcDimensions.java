package fr.igm.robotmissions.objects.ifc;

import java.io.Serializable;

public class IfcDimensions implements Serializable {

    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;

    public double getxMax() {
        return xMax;
    }

    public double getxMin() {
        return xMin;
    }

    public double getyMax() {
        return yMax;
    }

    public double getyMin() {
        return yMin;
    }


    public double getWidth(){
        return Math.abs(xMax-xMin);
    }
    public double getHeight(){
        return Math.abs(yMax-yMin);
    }


    @Override
    public String toString() {
        return "IfcDimensions{" +
                "xMin=" + xMin +
                ", xMax=" + xMax +
                ", yMin=" + yMin +
                ", yMax=" + yMax +
                '}';
    }
}
