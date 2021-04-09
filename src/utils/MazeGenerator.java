package utils;

import transforms.Point3D;

import java.util.Random;

public class MazeGenerator {

    /*
     * Popis algoritmu:
     * Zaplníme celý graf body typu "wall". Určíme počáteční bod, přepneme typ na "passage" - v této implementaci nodes
     * Přidáme do seznamu sousedy dáného bodu, vybereme náhodně ze seznamu a zkoušíme zda nový bod je soused dvou "passages"
     * Pokud podmínka platí, zahazujeme tento prvek seznamu a pokračujeme novým
     * V případě, že je soused pouze jedna "passage" - boříme zeď v daném bodě a odstraňujeme daný bod ze seznamu sousedů.
     *
     * */
    public static class Node {
        int x;
        int y;
        Node parent;
        char c;
        char dirs;
    }

    private Node[] nodes;
    private Point3D start;
    private Point3D end;
    private int width;
    private int height;
    private Random rand;
    private boolean[][] walls;

    public MazeGenerator() {
    }

    public void generateMaze(int sideLength) {
        this.width = sideLength;
        this.height = sideLength;

        init();
        generate();
        randomizeStartAndEnd();
        setWalls();
    }

    public boolean[][] getWalls() {
        return walls;
    }

    public Point3D getStart() {
        return start;
    }

    public Point3D getEnd() {
        return end;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void printMaze() { // debug - print maze
        for (int s = 0; s < height; s++) {
            for (int x = 0; x < width; x++) {
                Node node = nodes[x + s * width];

                System.out.print(node.c + " ");
            }
            System.out.println();
        }
    }

    private void init() {
        int i, j;
        Node b;

        rand = new Random();
        nodes = new Node[width * height];

        // init - seznam bodu
        for (i = 0; i < width * height; i++) {
            nodes[i] = new Node();
        }

        //nastaveni bodu
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                b = nodes[i + j * width];
                if (i * j % 2 != 0) {
                    b.x = i;
                    b.y = j;
                    b.dirs = 15; //binarne vsechny smery
                    b.c = ' ';
                } else {
                    b.c = '#'; //pridani zdi mezi body
                }
            }
        }
    }

    private void generate() {
        //nastavi startovaci bod
        Node start = nodes[1 + width];
        start.parent = start;
        Node last = start;

        //propojovani bodu dokud se nedostaneme do startovaciho bodu
        while ((last = link(last)) != start) ;
    }

    private Node link(Node n) {
        //spoji bod se svym sousedem (pokud mozno) a vrati odkaz na dalsi bod k prozkoumani
        int x = 0;
        int y = 0;
        char dir;
        Node dest;

        //nelze nic delat pokud je node null - nemelo by v algoritmu nastat
        if (n == null) return null;

        //pokud jsou neprozkoumane smery
        while (n.dirs != 0) {
            //Nahodny vyber promenne - viz nahodny jarnikuv algoritmus
            dir = (char) (1 << (rand.nextInt(Integer.MAX_VALUE) % 4));

            //pokud prozkoumano - vratim se
            if ((~n.dirs & dir) != 0) continue;

            //oznaceni prozkoumaneho smeru - ~ invertovani nul a jednicek v binaru - &= slouzi jako bit-AND a prirazeni hodnoty cili n.dirs = n.dirs & ~dir
            n.dirs &= ~dir;

            //rozhodovani dle smeru
            switch (dir) {
                //moznost jit doprava
                case 1:
                    if (n.x + 2 < width) {
                        x = n.x + 2;
                        y = n.y;
                    } else continue;
                    break;

                //moznost jit dolu
                case 2:
                    if (n.y + 2 < height) {
                        x = n.x;
                        y = n.y + 2;
                    } else continue;
                    break;

                //moznost jit doleva
                case 4:
                    if (n.x - 2 >= 0) {
                        x = n.x - 2;
                        y = n.y;
                    } else continue;
                    break;

                //moznost jit nahoru
                case 8:
                    if (n.y - 2 >= 0) {
                        x = n.x;
                        y = n.y - 2;
                    } else continue;
                    break;
            }

            //cilovy bod
            dest = nodes[x + y * width];

            //pokud se nejedna o zed
            if (dest.c == ' ') {
                //pokud je propoj - return
                if (dest.parent != null) continue;

                //nastavi nodu predchudce
                dest.parent = n;

                //odstrani zed mezi nody
                nodes[n.x + (x - n.x) / 2 + (n.y + (y - n.y) / 2) * width].c = ' ';

                //vraceni child node
                return dest;
            }
        }

        //pokud nelze nic dalsiho provest -> vraceni k predchudci
        return n.parent;
    }

    private void randomizeStartAndEnd() {
        Node startNode;
        Node endNode = nodes[0];

        int startIndex;
        int maxDistance = 0;

        // nahodny startovni bod
        do {
            startIndex = rand.nextInt(nodes.length - 1);
            startNode = nodes[startIndex];
        }
        while (startNode.c == '#' || (startNode.x == 0 && startNode.y == 0));

        // nastaveni nejvzdalenejsiho bodu v zavislosti na startovnim
        for (int i = 0; i < nodes.length - 1; i++) {
            Node node = nodes[i];

            if (node.c == '#' || (node.x == 0 && node.y == 0) || i == startIndex) {
                continue;
            }

            // vypocet vzdalenosti
            int distance = Math.abs(startNode.x - node.x) + Math.abs(startNode.y - node.y);

            if (maxDistance < distance) {
                maxDistance = distance;
                endNode = node;
            }
        }

        startNode.c = 'B';
        endNode.c = 'E';

        start = new Point3D(startNode.x, 0, startNode.y);
        end = new Point3D(endNode.x, 0, endNode.y);
    }

    private void setWalls() {
        walls = new boolean[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                walls[i][j] = nodes[j + i * width].c == '#';
            }
        }
    }

}