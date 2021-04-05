package models;
import transforms.Point3D;
import transforms.Vec3D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Wall {
    private float size = 1f;
    private Point3D position;

    /*private lwjglutils.OGLTexture2D texture;
    private lwjglutils.OGLTexture2D[] textureTriangle;*/

    public Wall(Point3D position,float size){
        this.position = position;

        /*textureTriangle = new lwjglutils.OGLTexture2D[3];
        try {
            texture = new lwjglutils.OGLTexture2D("textures/wall.jpg");
            textureTriangle[0] = new lwjglutils.OGLTexture2D("textures/textures/wall.jpg");
            textureTriangle[1] = new lwjglutils.OGLTexture2D("textures/textures/wall.jpg");
            textureTriangle[2] = new lwjglutils.OGLTexture2D("textures/textures/wall.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        for(int i = 1; i <= 3;i++){
            glBegin(GL_TRIANGLES); // front side

            glVertex3d(position.getX(),position.getY()*(i),position.getZ());

            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());

            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ());

            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());

            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ());

            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ());
            glEnd();
        }

        for(int i = 1; i <= 3;i++){
            glBegin(GL_TRIANGLES); // right side

            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ());
            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ());
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);

            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ());
            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);
            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ()+size);
            glEnd();
        }
        for(int i = 1; i <= 3;i++){
            glBegin(GL_TRIANGLES); // left side

            glVertex3d(position.getX(),position.getY()*(i),position.getZ());
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());
            glVertex3d(position.getX(),position.getY()*(i),position.getZ()+size);

            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ());
            glVertex3d(position.getX(),position.getY()*(i),position.getZ()+size);
            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ()+size);
            glEnd();
        }


        for(int i = 1; i <= 3;i++){
            glBegin(GL_TRIANGLES); // front side

            glVertex3d(position.getX(),position.getY()*(i),position.getZ()+size);

            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ()+size);

            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);

            glVertex3d(position.getX(),position.getY()+(size*i),position.getZ()+size);

            glVertex3d(position.getX()+size,position.getY()*(i),position.getZ()+size);

            glVertex3d(position.getX()+size,position.getY()+(size*i),position.getZ()+size);
            glEnd();
        }

        // uzavreni objektu - top
        glBegin(GL_TRIANGLES); // dohromady tvori ctverec
        glVertex3d(position.getX(), position.getY()+(size*3), position.getZ());

        glVertex3d(position.getX()+size, position.getY()+(size*3), position.getZ());

        glVertex3d(position.getX(), position.getY()+(size*3), position.getZ()+size);

        glVertex3d(position.getX()+size, position.getY()+(size*3), position.getZ());

        glVertex3d(position.getX(), position.getY()+(size*3), position.getZ()+size);
        glVertex3d(position.getX()+size, position.getY()+(size*3), position.getZ()+size);
        glEnd();


    }
    public boolean isColission(Vec3D camPos){
        if((camPos.getX() == position.getX() && camPos.getZ() == position.getZ()) || (camPos.getX() == position.getX()+size && camPos.getZ() == position.getZ()) || (camPos.getX() == position.getX() && camPos.getZ() == position.getZ() +size) || (camPos.getX() == position.getX() +size && camPos.getZ() == position.getZ() +size)){
            return true;
        }else{
        return false;
        }
    }
}
