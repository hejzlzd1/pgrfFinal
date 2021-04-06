package models;

import static org.lwjgl.opengl.GL11.*;

import lwjglutils.OGLTexture2D;
import transforms.Point3D;

public class Floor {
    private float size = 1;
    public Floor(Point3D position, float size, int w, int h, OGLTexture2D floorTexture){
        for(double i = position.getX(); i < w;i++) {
            for (double i2 = position.getZ(); i2 < h; i2++) {
                glEnable(GL_TEXTURE_2D);
                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

                floorTexture.bind();
                glBegin(GL_TRIANGLES); // dohromady tvori ctverec

                glTexCoord2f(0.0f, 0.0f);
                glColor3f(1f,1f,1f);
                glVertex3d(i, position.getY(), i2);

                glColor3f(1f,1f,1f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3d(i+size, position.getY(), i2);

                glColor3f(1f,1f,1f);
                glTexCoord2f(0.0f, 1.0f);
                glVertex3d(i, position.getY(), i2+size);

                glColor3f(1f,1f,1f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3d(i+size, position.getY(), i2);

                glTexCoord2f(0.0f, 1.0f);
                glColor3f(1f,1f,1f);
                glVertex3d(i, position.getY(), i2+size);

                glColor3f(1f,1f,1f);
                glTexCoord2f(1.0f, 1.0f);
                glVertex3d(i + size, position.getY(), i2 + size);
                glEnd();

                glDisable(GL_TEXTURE_2D);
            }
        }
    }
}
