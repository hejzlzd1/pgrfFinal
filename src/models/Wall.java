package models;
import transforms.Point3D;
import transforms.Vec3D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Wall {
    private float size = 1f;
    private Point3D position;

    public Wall(Point3D position, float size, lwjglutils.OGLTexture2D wallTexture){
        this.position = position;

        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        for(int i = 1; i <= 2;i++){
            wallTexture.bind();
            glBegin(GL_TRIANGLES); // front side
            glColor3f(1f,0.5f,0f);
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()*(i),position.getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ());
            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ());
            glEnd();
        }

        for(int i = 1; i <= 2;i++){
            wallTexture.bind();
            glBegin(GL_TRIANGLES); // right side
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);
            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ()+size);
            glEnd();
        }
        for(int i = 1; i <= 2;i++){
            wallTexture.bind();
            glBegin(GL_TRIANGLES); // left side
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()*(i),position.getZ());
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(),position.getY()*(i),position.getZ()+size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX(),position.getY()*(i),position.getZ()+size);
            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ()+size);
            glEnd();
        }


        for(int i = 1; i <= 2;i++){
            wallTexture.bind();
            glBegin(GL_TRIANGLES); // front side
            glTexCoord2f(0.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()*(i),position.getZ()+size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ()+size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);
            glTexCoord2f(1.0f, 0.0f);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ()+size);
            glTexCoord2f(0.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);
            glTexCoord2f(1.0f, 1.0f);
            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ()+size);
            glEnd();
        }

        // uzavreni objektu - top
        wallTexture.bind();
        glBegin(GL_TRIANGLES); // dohromady tvori ctverec
        glTexCoord2f(0.0f, 0.0f);
        glVertex3d(position.getX(), position.getY()+(size*2), position.getZ());
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(position.getX()+size, position.getY()+(size*2), position.getZ());
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(position.getX(), position.getY()+(size*2), position.getZ()+size);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3d(position.getX()+size, position.getY()+(size*2), position.getZ());
        glTexCoord2f(0.0f, 1.0f);
        glVertex3d(position.getX(), position.getY()+(size*2), position.getZ()+size);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3d(position.getX()+size, position.getY()+(size*2), position.getZ()+size);
        glEnd();

        glDisable(GL_TEXTURE_2D);
    }
}
