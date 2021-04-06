package utils;

import lwjglutils.OGLTexture2D;
import models.Score;
import models.Wall;
import transforms.Point3D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MazeProcessor {
    public static List<Score> getScoreList() {
        return scoreList;
    }

    private static List<Score> scoreList;
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
    public static void createScore(boolean[][] walls, int maxScore){
        scoreList = new ArrayList<>();
        Random rnd = new Random();
        while(scoreList.size() <= maxScore){
            int x = rnd.nextInt(walls.length);
            int z = rnd.nextInt(walls.length);
            if(!walls[x][z]){
                scoreList.add(new Score(new Point3D(x,1,z),1));
            }
        }
    }
}
