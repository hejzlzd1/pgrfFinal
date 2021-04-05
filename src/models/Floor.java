package models;

import static org.lwjgl.opengl.GL11.*;
import transforms.Point3D;

public class Floor {
    private float size = 1;
    public Floor(Point3D position, float size, int w, int h){
        for(double i = position.getX(); i < w;i++) {
            for (double i2 = position.getZ(); i2 < h; i2++) {
                glBegin(GL_TRIANGLES); // dohromady tvori ctverec
                glColor3f(1f,1f,1f);
                glVertex3d(i, position.getY(), i2);
                glColor3f(1f,1f,1f);
                glVertex3d(i+size, position.getY(), i2);
                glColor3f(1f,1f,1f);
                glVertex3d(i, position.getY(), i2+size);
                glColor3f(1f,1f,1f);
                glVertex3d(i+size, position.getY(), i2);
                glColor3f(1f,1f,1f);
                glVertex3d(i, position.getY(), i2+size);
                glColor3f(1f,1f,1f);
                glVertex3d(i + size, position.getY(), i2 + size);
                glEnd();
            }
        }
    }
}
