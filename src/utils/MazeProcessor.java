package utils;

import lwjglutils.OGLTexture2D;
import models.Score;
import models.Wall;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MazeProcessor {
    public List<Wall> wallList = new ArrayList<>();
    private List<Score> scoreList = new ArrayList<>();

    public List<Score> getScoreList() {
        return scoreList;
    }

    public List<Wall> getWallList() {
        return wallList;
    }

    public void createWalls(boolean[][] walls, OGLTexture2D wallTexture) {

        for (int i = 0; i < walls.length; i++) {
            for (int j = 0; j < walls.length; j++) {
                if (walls[i][j]) {
                    wallList.add(new Wall(new Point3D(i, 0, j), 1));
                }
            }
        }
        for (int i = 0; i <= walls.length; i++) {
            wallList.add(new Wall(new Point3D(i, 0, walls.length), 1));

        }
        for (int j = 0; j <= walls.length; j++) {
            wallList.add(new Wall(new Point3D(walls.length, 0, j), 1));
        }
    }

    public void createScore(boolean[][] walls, int maxScore) {
        scoreList = new ArrayList<>();
        Random rnd = new Random();
        while (scoreList.size() < maxScore) {
            int x = rnd.nextInt(walls.length);
            int z = rnd.nextInt(walls.length);
            if (!walls[x][z]) {
                boolean scoreIsThere = false;
                for (Score sc : scoreList) {
                    if (sc.getPosition().getX() == x && sc.getPosition().getZ() == z) {
                        scoreIsThere = true;
                        break;
                    }
                }
                if (!scoreIsThere) scoreList.add(new Score(new Point3D(x + 0.5, 1, z + 0.5), 0.2f));
            }
        }
    }
}
