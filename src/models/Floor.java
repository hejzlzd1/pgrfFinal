package models;

import lwjglutils.OGLTexture2D;
import transforms.Point3D;

import static org.lwjgl.opengl.GL11.*;

public class Floor extends GameObject {
    private final float size;
    private final int w; //width
    private final int h; //height

    public Floor(Point3D position, float size, int w, int h) {
        setPosition(position);
        this.size = size;
        this.w = w;
        this.h = h;
    }

    public void renderFloor(OGLTexture2D floorTexture) {
        for (double i = getPosition().getX(); i < w; i++) {
            for (double i2 = getPosition().getZ(); i2 < h; i2++) {
                glEnable(GL_TEXTURE_2D);
                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

                floorTexture.bind();
                glBegin(GL_TRIANGLE_STRIP); // dohromady tvori ctverec

                glTexCoord2f(0.0f, 0.0f);
                glColor3f(1f, 1f, 1f);
                glVertex3d(i, getPosition().getY(), i2);

                glColor3f(1f, 1f, 1f);
                glTexCoord2f(0.0f, 1.0f);
                glVertex3d(i, getPosition().getY(), i2 + size);

                glColor3f(1f, 1f, 1f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3d(i + size, getPosition().getY(), i2);


                glTexCoord2f(0.0f, 1.0f);
                glColor3f(1f, 1f, 1f);
                glVertex3d(i, getPosition().getY(), i2 + size);

                glColor3f(1f, 1f, 1f);
                glTexCoord2f(1.0f, 0.0f);
                glVertex3d(i + size, getPosition().getY(), i2);

                glColor3f(1f, 1f, 1f);
                glTexCoord2f(1.0f, 1.0f);
                glVertex3d(i + size, getPosition().getY(), i2 + size);
                glEnd();

                glDisable(GL_TEXTURE_2D);
            }
        }
    }
}
