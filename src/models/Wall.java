package models;

import transforms.Point3D;

import static org.lwjgl.opengl.GL11.*;

public class Wall extends GameObject {
    private final float size;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int height = 1;

    public Wall(Point3D pos, float size) {
        setPosition(pos);
        this.size = size;
    }

    public void renderWall(lwjglutils.OGLTexture2D wallTexture) {
        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        //TODO: lightning - add normal to walls


        for (int i = 0; i <= height; i++) { // cyklus urcuje vysku zdi
            wallTexture.bind();
            glBegin(GL_TRIANGLE_STRIP); // front side
            glNormal3f(0, 0, -1);

            glColor3f(1f, 0.55f, 0f);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + i, getPosition().getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (size) + i, getPosition().getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + i, getPosition().getZ());

            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (size) + i, getPosition().getZ());

            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX() + size, (getPosition().getY() + i), getPosition().getZ());


            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(getPosition().getX() + size, (getPosition().getY() + (size) + i), getPosition().getZ());
            glEnd();

        }

        for (int i = 0; i <= height; i++) { //right
            glBegin(GL_TRIANGLE_STRIP);
            glNormal3f(1, 0, 0);

            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (size + i), getPosition().getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (i), getPosition().getZ() + size);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (i), getPosition().getZ());

            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (size) + i, getPosition().getZ());

            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (i), getPosition().getZ() + size);
            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (size) + i, getPosition().getZ() + size);
            glEnd();
        }

        for (int i = 0; i <= height; i++) {
            glBegin(GL_TRIANGLE_STRIP); //zadni strana
            glNormal3f(0, 0, 1);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (i), getPosition().getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (i), getPosition().getZ() + size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (size + i), getPosition().getZ());


            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (i), getPosition().getZ() + size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (size + i), getPosition().getZ());

            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (size + i), getPosition().getZ() + size);

            glEnd();
        }


        for (int i = 0; i <= height; i++) {
            glBegin(GL_TRIANGLE_STRIP); //left
            glNormal3f(-1, 0, 0);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (i), getPosition().getZ() + size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (i), getPosition().getZ() + size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (size + i), getPosition().getZ() + size);

            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (size + i), getPosition().getZ() + size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(getPosition().getX() + size, getPosition().getY() + (i), getPosition().getZ() + size);

            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(getPosition().getX(), getPosition().getY() + (size + i), getPosition().getZ() + size);
            glEnd();
        }

        // uzavreni objektu - top

        glBegin(GL_TRIANGLE_STRIP);
        glNormal3f(0, 1, 0);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(getPosition().getX(), getPosition().getY() + (size + height), getPosition().getZ());
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(getPosition().getX(), getPosition().getY() + (size + height), getPosition().getZ() + size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(getPosition().getX() + size, getPosition().getY() + (size + height), getPosition().getZ());


        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(getPosition().getX(), getPosition().getY() + (size + height), getPosition().getZ() + size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(getPosition().getX() + size, getPosition().getY() + (size + height), getPosition().getZ());
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(getPosition().getX() + size, getPosition().getY() + (size + height), getPosition().getZ() + size);
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }
}
