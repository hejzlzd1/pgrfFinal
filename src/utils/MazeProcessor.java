package utils;

import models.Wall;
import transforms.Point3D;

public class MazeProcessor {

    public static void createWalls(boolean[][] walls) {
        for (int i = 0; i < walls.length; i++)
        {
            for (int j = 0; j < walls.length; j++)
            {
                if(walls[i][j]){
                    new Wall(new Point3D(i,0,j),1);
                }
            }
        }
        for(int i = 0; i <= walls.length; i++){
            new Wall(new Point3D(i,0, walls.length),1);
        }
        for(int j = 0; j <= walls.length; j++){
            new Wall(new Point3D(walls.length,0, j),1);
        }
    }
}
