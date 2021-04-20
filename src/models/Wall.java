package models;

import transforms.Point3D;

import static org.lwjgl.opengl.GL11.*;

public class Wall {
    private float size = 1f;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int height = 1;
    private Point3D position;

    public Wall(Point3D pos, float size, lwjglutils.OGLTexture2D wallTexture) {
        this.position = pos;
        this.size = size;

        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        //TODO: lightning - add normal to walls


        for (int i = 0; i <= height; i++) { // cyklus urcuje vysku zdi
            wallTexture.bind();
            glBegin(GL_TRIANGLE_STRIP); // front side
            glColor3f(1f, 0.55f, 0f);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX(), position.getY() + i, position.getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(), position.getY() + (size) + i, position.getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX() + size, position.getY() + i, position.getZ());

            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(), position.getY() + (size) + i, position.getZ());

            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX() + size, (position.getY() + i), position.getZ());



            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX() + size, (position.getY() + (size) + i), position.getZ());
            glEnd();

        }

        for (int i = 0; i <= height; i++) { //left
            glBegin(GL_TRIANGLE_STRIP);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX() + size, position.getY() + (size + i), position.getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX() + size, position.getY() + (i), position.getZ() + size);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX() + size, position.getY() + (i), position.getZ());

            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX() + size, position.getY() + (size) + i, position.getZ());

            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX() + size, position.getY() + (i), position.getZ() + size);
            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX() + size, position.getY() + (size) + i, position.getZ() + size);
            glEnd();
        }

        for (int i = 0; i <= height; i++) {
            glBegin(GL_TRIANGLE_STRIP);

            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX(), position.getY() + (i), position.getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(), position.getY() + (i), position.getZ() + size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(), position.getY() + (size + i), position.getZ());


            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(), position.getY() + (i), position.getZ() + size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(), position.getY() + (size + i), position.getZ());

            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX(), position.getY() + (size + i), position.getZ() + size);

            glEnd();
        }


        for (int i = 0; i <= height; i++) {
            glBegin(GL_TRIANGLE_STRIP);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX(), position.getY() + (i), position.getZ() + size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX() + size, position.getY() + (i), position.getZ() + size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(), position.getY() + (size + i), position.getZ() + size);

            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX() + size, position.getY() + (size + i), position.getZ() + size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX() + size, position.getY() + (i), position.getZ() + size);

            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(), position.getY() + (size + i), position.getZ() + size);
            glEnd();
        }

        // uzavreni objektu - top

        glBegin(GL_TRIANGLE_STRIP);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(position.getX(), position.getY() + (size + height), position.getZ());
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(position.getX(), position.getY() + (size + height), position.getZ() + size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(position.getX() + size, position.getY() + (size + height), position.getZ());


        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(position.getX(), position.getY() + (size + height), position.getZ() + size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(position.getX() + size, position.getY() + (size + height), position.getZ());
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(position.getX() + size, position.getY() + (size + height), position.getZ() + size);
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }
}
