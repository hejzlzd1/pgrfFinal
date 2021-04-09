package models;
import transforms.Point3D;

public class Score {
    private Point3D position;
    private float size;

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Score(transforms.Point3D position, float size) {
        this.position = position;
        this.size = size;
    }
}
