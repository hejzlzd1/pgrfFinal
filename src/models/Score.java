package models;
import static org.lwjgl.opengl.GL11.*;

public class Score {

    public Score(transforms.Point3D position, float size) {
        glBegin(GL_TRIANGLES); // dohromady tvori ctverec
        glVertex3d(position.getX()-0.5, position.getY(), position.getZ()-0.5);

        glVertex3d(position.getX() +0.5, position.getY(), position.getZ()+0.5);

        glVertex3d(position.getX(), position.getY() + 0.5, position.getZ() + size);

        glVertex3d(position.getX() + size, position.getY() + (size * 3), position.getZ());

        glVertex3d(position.getX(), position.getY() + (size * 3), position.getZ() + size);
        glVertex3d(position.getX() + size, position.getY() + (size * 3), position.getZ() + size);
        glEnd();
    }
}
