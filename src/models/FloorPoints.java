package models;

import extension.global.GLCamera;
import transforms.Point3D;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glTexParameteri;


public class FloorPoints {
    private Point3D position;

    public FloorPoints(Point3D position, lwjglutils.OGLTexture2D texture) {
        this.position = position;
        glEnable(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPLACE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        texture.bind();

        glMatrixMode(GL_TEXTURE);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glBegin(GL_QUADS);
        glTexCoord2f(0f, 0f);
        glVertex3f((float) position.getX(), 0.01f, (float) position.getZ());
        glTexCoord2f(0.0f, 1f);
        glVertex3f((float) position.getX(), 0.01f, (float) position.getZ() + 1f);
        glTexCoord2f(1f, 1f);
        glVertex3f((float) position.getX() + 1f, 0.01f, (float) position.getZ() + 1f);
        glTexCoord2f(1f, 0f);
        glVertex3f((float) position.getX() + 1f, 0.01f, (float) position.getZ());
        glEnd();
        glPopMatrix();

        glDisable(GL_TEXTURE_2D);
    }

    public boolean checkPositionWithCam(GLCamera cam) {
        return ((cam.getPosition().getX() >= position.getX() - 0.5 && cam.getPosition().getX() <= position.getX() + 0.5) && (cam.getPosition().getZ() >= position.getZ() - 0.5 && cam.getPosition().getZ() <= position.getZ() + 0.5));
    }
}
