package models;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static extension.global.GlutUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEndList;

public class Skybox {
    private OGLTexture2D texture;
    private OGLTexture2D[] textureCube;
    public Skybox(){
        textureCube = new lwjglutils.OGLTexture2D[6];
    }
    public void loadSkybox(){
        System.out.println("Loading textures...");
        try {
            texture = new lwjglutils.OGLTexture2D("textures/hot_up.png");
            textureCube[0] = new OGLTexture2D("textures/hot_ft.png");
            textureCube[1] = new OGLTexture2D("textures/hot_bk.png");
            textureCube[2] = new OGLTexture2D("textures/hot_up.png");
            textureCube[3] = new OGLTexture2D("textures/hot_dn.png");
            textureCube[4] = new OGLTexture2D("textures/hot_rt.png");
            textureCube[5] = new OGLTexture2D("textures/hot_lf.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        glNewList(2, GL_COMPILE);

        glPushMatrix();
        glColor3d(0.5, 0.5, 0.5);
        int size = 200;
        glutWireCube(size); //neni nutne, pouze pro znazorneni tvaru skyboxu

        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);


        textureCube[1].bind(); //-x  (left)
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(-size, -size, -size);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(-size, size, -size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(-size, size, size);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(-size, -size, size);
        glEnd();

        textureCube[0].bind();//+x  (right)
        glBegin(GL_QUADS);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(size, -size, -size);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(size, -size, size);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(size, size, size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(size, size, -size);
        glEnd();

        textureCube[3].bind(); //-y bottom
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(-size, -size, -size);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(size, -size, -size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(size, -size, size);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(-size, -size, size);
        glEnd();

        textureCube[2].bind(); //+y  top
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(-size, size, -size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(size, size, -size);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(size, size, size);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(-size, size, size);
        glEnd();

        textureCube[5].bind(); //-z
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(size, -size, -size);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(-size, -size, -size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(-size, size, -size);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(size, size, -size);
        glEnd();

        textureCube[4].bind(); //+z
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(-size, size, size);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(-size, -size, size);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(size, -size, size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(size, size, size);
        glEnd();

        glDisable(GL_TEXTURE_2D);
        glPopMatrix();

        glEndList();
    }
    }
