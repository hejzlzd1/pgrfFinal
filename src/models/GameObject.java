package models;

import extension.global.GLCamera;
import transforms.Point3D;

public class GameObject {
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

    public boolean isNearCamera(GLCamera camera, float radius) {
        return (camera.getPosition().getX() >= position.getX() - radius && camera.getPosition().getX() <= position.getX() + radius) && (camera.getPosition().getZ() >= position.getZ() - radius && camera.getPosition().getZ() <= position.getZ() + radius);
    }
}
