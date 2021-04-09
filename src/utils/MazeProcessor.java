package utils;

import lwjglutils.OGLTexture2D;
import models.Score;
import models.Wall;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static extension.global.GlutUtils.glutSolidCube;
import static extension.global.GlutUtils.glutSolidSphere;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;


public class MazeProcessor {
    private static List<Score> scoreList = new ArrayList<>();
    public static List<Score> getScoreList() {
        return scoreList;
    }


    public static void createWalls(boolean[][] walls, OGLTexture2D wallTexture) {

        for (int i = 0; i < walls.length; i++)
        {
            for (int j = 0; j < walls.length; j++)
            {
                if(walls[i][j]){
                    new Wall(new Point3D(i,0,j),1,wallTexture);
                }
            }
        }
        for(int i = 0; i <= walls.length; i++){
            new Wall(new Point3D(i,0, walls.length),1,wallTexture);
        }
        for(int j = 0; j <= walls.length; j++){
            new Wall(new Point3D(walls.length,0, j),1,wallTexture);
        }
    }
    public static void createScore(boolean[][] walls, int maxScore, OGLTexture2D scoreTexture){
        if(scoreList.isEmpty()){
        scoreList = new ArrayList<>();
        Random rnd = new Random();
        while(scoreList.size() <= maxScore){
            int x = rnd.nextInt(walls.length);
            int z = rnd.nextInt(walls.length);
            if(!walls[x][z]){
                boolean scoreIsThere = false;
                for(Score sc : scoreList){
                    if(sc.getPosition().getX() == x && sc.getPosition().getZ() == z) scoreIsThere=true;
                }
                if(!scoreIsThere) scoreList.add(new Score(new Point3D(x+0.5,1,z+0.5),0.2f));
            }
        }
        }else{
            for(Score sc : scoreList){
                glEnable(GL_TEXTURE_2D);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPLACE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPLACE);
                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
                scoreTexture.bind();

                glMatrixMode(GL_TEXTURE);
                glLoadIdentity();
                glMatrixMode(GL_MODELVIEW);
                glPushMatrix();
                glTranslatef((float)sc.getPosition().getX(), (float) sc.getPosition().getY(), (float) sc.getPosition().getZ());
                glColor3f(0.9f, 0.1f, 0.1f);
                glutSolidSphere(sc.getSize(), 20, 20);// Koule
                glPopMatrix();

                glDisable(GL_TEXTURE_2D);
            }
        }
    }
}
