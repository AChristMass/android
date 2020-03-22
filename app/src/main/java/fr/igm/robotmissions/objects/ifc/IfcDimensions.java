package fr.igm.robotmissions.objects.ifc;

import java.io.Serializable;

public class IfcDimensions implements Serializable {

    //declarer les dimentions des murs pour calculer la dimension de l'ifc
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

//calcul de la largeur de la piece
    public double getWidth(){
        return Math.abs(xMax-xMin);
    }
//calcul de la longueur de la piece
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
