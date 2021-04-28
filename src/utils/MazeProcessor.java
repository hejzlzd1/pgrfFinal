package utils;

import models.Score;
import models.Wall;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MazeProcessor {
    public List<Wall> wallList = new ArrayList<>(); // ukládání zdí
    private List<Score> scoreList = new ArrayList<>(); // ukládání skóre

    // Gettery pro uložené zdi/skóre
    public List<Score> getScoreList() {
        return scoreList;
    }

    public List<Wall> getWallList() {
        return wallList;
    }

    public void createWalls(boolean[][] walls) { // tvorba zdí dle předgenerovaného labyrintu

        for (int i = 0; i < walls.length; i++) { // dvou rozměrné pole -> potřebuji dva cykly
            for (int j = 0; j < walls.length; j++) {
                if (walls[i][j]) {
                    wallList.add(new Wall(new Point3D(i, 0, j), 1)); // zeď rovnou přidám do datové struktury
                }
            }
        }
        for (int i = 0; i <= walls.length; i++) { // Algoritmus pro tvorbu labyrintu mi vyhazoval pole bez hranic "dole" a "vpravo" - implementováno toto řešení, aby se neprojevil problém při renderu
            wallList.add(new Wall(new Point3D(i, 0, walls.length), 1));
        }
        for (int j = 0; j <= walls.length; j++) {
            wallList.add(new Wall(new Point3D(walls.length, 0, j), 1));
        }
    }

    public void createScore(boolean[][] walls, int maxScore) { // generuje skóre na základě nějakého maximálního počtu
        scoreList = new ArrayList<>(); // inicializace, aby v případě clearu nenastal problém -> bez nového skóre
        Random rnd = new Random();
        while (scoreList.size() < maxScore) {
            int x = rnd.nextInt(walls.length); //generuji pozici s max velikostí labyrintu
            int z = rnd.nextInt(walls.length);
            if (!walls[x][z]) { // pokud na dané pozici není zeď
                boolean scoreIsThere = false;
                for (Score sc : scoreList) { // zkoumám zda se na pozici nenachází skóre
                    if (sc.getPosition().getX() == x && sc.getPosition().getZ() == z) {
                        scoreIsThere = true;
                        break; // optimalizace - vyskakuji z cyklu pokud se na dané pozici nachází nějaký z bodů
                    }
                }
                if (!scoreIsThere)
                    scoreList.add(new Score(new Point3D(x + 0.5, 1, z + 0.5), 0.2f)); // pokud nenastane problém - vygeneruje bod a dá do pole
            }
        }
    }
}
