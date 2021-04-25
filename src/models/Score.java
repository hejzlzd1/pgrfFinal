package models;

import extension.global.GLCamera;

import static extension.global.GlutUtils.glutSolidSphere;
import static org.lwjgl.opengl.GL11.*;

public class Score extends GameObject {

    private boolean shouldShrink;
    private boolean shouldFlyDown;


    public Score(transforms.Point3D position, float size) {
        setPosition(position);
        setSize(size);
        shouldShrink = true;
        shouldFlyDown = true;
    }

    public boolean checkPositionWithCam(GLCamera cam) {
        return ((cam.getPosition().getX() >= getPosition().getX() - 0.5 && cam.getPosition().getX() <= getPosition().getX() + 0.5) && (cam.getPosition().getZ() >= getPosition().getZ() - 0.5 && cam.getPosition().getZ() <= getPosition().getZ() + 0.5));
    }

    public void renderScore(lwjglutils.OGLTexture2D scoreTexture) {
        glEnable(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPLACE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        scoreTexture.bind();

        glMatrixMode(GL_TEXTURE);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glTranslatef((float) getPosition().getX(), (float) getPosition().getY(), (float) getPosition().getZ());
        glColor3f(0.9f, 0.1f, 0.1f);
        glutSolidSphere(getSize(), 20, 20);// Koule
        glPopMatrix();

        glDisable(GL_TEXTURE_2D);
    }

    public boolean isShouldShrink() {
        return shouldShrink;
    }

    public boolean isShouldFlyDown() {
        return shouldFlyDown;
    }

    public void setShouldFlyDown(boolean shouldFlyDown) {
        this.shouldFlyDown = shouldFlyDown;
    }

    public void setShouldShrink(boolean shouldShrink) {
        this.shouldShrink = shouldShrink;
    }
}
