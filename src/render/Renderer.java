package render;

import extension.global.AbstractRenderer;
import extension.global.GLCamera;
import models.Floor;
import models.FloorPoints;
import models.Score;
import models.Wall;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import transforms.Point3D;
import transforms.Vec3D;
import utils.MazeGenerator;
import utils.MazeProcessor;
import utils.ShrinkScoreUtil;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.util.Random;

import static extension.global.GluUtils.gluPerspective;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Renderer extends AbstractRenderer {
    private final float kA = 0.5f;
    private final float kD = 0.5f;
    private final float kS = 0.5f;
    private final float kH = 10;
    private float dx;
    private float dy;
    private float ox;
    private float oy;
    private float zenit;
    private float azimut;
    private float playerHeight;
    private float deltaTrans = 0;
    private float kE;
    private lwjglutils.OGLTexture2D floorTexture, wallTexture, scoreTexture, startTexture, endTexture;
    private int collectedScore, maxScoreGenerated, mazeSize = 20, cameraPressKey;
    private boolean walkDown, gameEnded, flightMode, mouseButton1 = false, renderInRadius;
    private double zenith;
    private GLCamera camera;
    private MazeGenerator mg;
    private String scoreInfo;
    private FloorPoints endPoint, startPoint;
    private MazeProcessor mp;
    private Floor fl;
    private ShrinkScoreUtil ssu;
    private long startTime, endTime, estimatedTime;


    public Renderer() {
        super();

        /*used default glfwWindowSizeCallback see AbstractRenderer*/

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);

                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_W, GLFW_KEY_D, GLFW_KEY_S, GLFW_KEY_A -> deltaTrans = 0.05f; // nastaví rychlost pohybu při stisku klávesy
                        case GLFW_KEY_LEFT_SHIFT -> deltaTrans = 0.08f; // nastaví rychlost pohybu při sprintu
                        case GLFW_KEY_M -> { // přepne do "leteckého" módu - nahoru a dolu pomocí R a F
                            if (flightMode) {
                                camera.setPosition(camera.getPosition().withY(1.5)); // usadí zpět na zem pokud se vypne letecký mód
                            }
                            flightMode = !flightMode;
                        }

                        case GLFW_KEY_F1 -> { //debug mod - povoli litani, vypne mlhu, osvetleni a nastavi render mimo radius
                            gameEnded = !gameEnded;
                            renderInRadius = !renderInRadius;
                            glDisable(GL_FOG);

                            camera.setPosition(new Vec3D(mg.getStart().getX() + 0.5, 1.5, mg.getStart().getZ() + 0.5)); // vrátí kamerui na start (i v přepnutí do klasického módu
                        }
                        //help a restart
                        case GLFW_KEY_H -> JOptionPane.showMessageDialog(null, "H - Toto okno \n WSAD - Pohyb \n M - Přepínání mezi pohybem a levitací \n R - Let nahoru \n F - Let dolu \n C - reset \n Levý shift - Sprint (při pohybu) \n F1 - debug mód (zpřístupní mód po dokončení labyrintu - při vypnutí se vrací do původního stavu)");
                        case GLFW_KEY_C -> init();
                    }
                }
                switch (key) { // pohyb provádím každý snímek kvůli plynulosti, zde jen nastavuji proměnnou pro rozhodnutí o pohybu
                    case GLFW_KEY_W:
                        cameraPressKey = 1;
                        break;
                    case GLFW_KEY_S:
                        cameraPressKey = 2;
                        break;
                    case GLFW_KEY_A:
                        cameraPressKey = 3;
                        break;
                    case GLFW_KEY_D:
                        cameraPressKey = 4;
                        break;
                    case GLFW_KEY_R: // let nahoru - omezeno na výšku 6
                        if (flightMode && camera.getPosition().getY() < 6)
                            camera.setPosition(camera.getPosition().withY(camera.getPosition().getY() + deltaTrans));
                        break;
                    case GLFW_KEY_F: // let dolu - omezeno na výšku 1.4
                        if (flightMode && camera.getPosition().getY() > 1.4) camera.down(deltaTrans);
                        break;

                }
                if (action == GLFW_RELEASE) { // puštění kláves
                    switch (key) {
                        case GLFW_KEY_W, GLFW_KEY_S, GLFW_KEY_A, GLFW_KEY_D -> cameraPressKey = 0; //deaktivování pohybu
                        case GLFW_KEY_LEFT_SHIFT -> deltaTrans = 0.05f;
                    }
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) { //pohled
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);

                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    ox = (float) x;
                    oy = (float) y;
                }
            }

        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) { //pohled
                if (mouseButton1) {
                    dx = (float) x - ox;
                    dy = (float) y - oy;
                    ox = (float) x;
                    oy = (float) y;
                    zenit -= dy / width * 180;
                    if (zenit > 90)
                        zenit = 90;
                    if (zenit <= -90)
                        zenit = -90;
                    azimut += dx / height * 180;
                    azimut = azimut % 360;
                    camera.setAzimuth(Math.toRadians(azimut));
                    camera.setZenith(Math.toRadians(zenit));
                    dx = 0;
                    dy = 0;
                }
            }
        };
    }


    @Override
    public void init() {
        super.init();
        settings(); // provede počáteční nastavení
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        setFog(); // zapne a nastaví mlhu

        cameraPressKey = 0; // nepohybuji se - init hodnota
        deltaTrans = 0.05f; // rychost pohybu
        gameEnded = false; // rozhodnutí o konci hry
        flightMode = false; // letecký mód
        renderInRadius = true; // renderování v radiusu

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        ssu = new ShrinkScoreUtil();
        mp = new MazeProcessor();

        mg = new MazeGenerator();
        if (mazeSize == -1) { // v případě, že uživatel chce náhodnou velikost bludiště
            mazeSize = new Random().nextInt(40 + 1 - 10) + 10;
        }

        mg.generateMaze(mazeSize); // generování bludiště

        startTime = System.currentTimeMillis(); // zahájení měření času
        maxScoreGenerated = new Random().nextInt(mazeSize + 1 - 1) + 1; // náhodný počet skóre 1 až velikost bludiště
        collectedScore = 0; // init posbíraného skóre
        mp.createScore(mg.getWalls(), maxScoreGenerated); // tvorba skóre dle maxima a vygenerovaného labyrintu
        mp.createWalls(mg.getWalls()); // tvorba zdí dle vygenerovaného labyrintu
        startPoint = new FloorPoints(mg.getStart()); // tvorba startovacího bodu dle předgenerované pozice v labyrintu
        endPoint = new FloorPoints(mg.getEnd()); // tvorba konečného bodu dle předgenerované pozice v labyrintu
        fl = new Floor(new Point3D(0, 0, 0), 1f, mg.getWidth() + 1, mg.getHeight() + 1); // tvorba podlahy dle velikosti hranic labyrintu

        ssu.initTimerForScoreSize(mp); // zapnutí animací u skóre

        camera = new GLCamera();
        camera.setPosition(new transforms.Vec3D(mg.getStart().getX() + 0.5, 1.5, mg.getStart().getZ() + 0.5)); //kamera na start
        camera.setFirstPerson(true);
        playerHeight = 1.5f; // výška pro dynamický pohyb
        walkDown = true; // rozhodující proměnná pro dynamický pohyb

        setLight(); // zapne a nastaví světlo

        try { // načte textury
            floorTexture = new lwjglutils.OGLTexture2D("textures/stonefloor.jpg");
            wallTexture = new lwjglutils.OGLTexture2D("textures/wall.jpg");
            scoreTexture = new lwjglutils.OGLTexture2D("textures/score.jpg");
            endTexture = new lwjglutils.OGLTexture2D("textures/endHole.png");
            startTexture = new lwjglutils.OGLTexture2D("textures/start.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFog() { // nastavení mlhy
        glEnable(GL_FOG);
        glFogi(GL_FOG_MODE, GL_LINEAR);
        glFogi(GL_FOG_START, (int) 0f);
        glFogi(GL_FOG_END, (int) 8f);
        glFogf(GL_FOG_DENSITY, 0.08f);
        glFogfv(GL_FOG_COLOR, new float[]{0.1f, 0.1f, 0.1f, 1});
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        setLight(); // nastavení světla
        moveCameraFPS(); // plynulá kamera - kontroluji zda se uživatel nepohybuje
        ssu.setCamera(camera); // předávám kameru pro animaci skóre (kvůli radiusu)

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        gluPerspective(70, width / (float) height, 0.01f, 500.0f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        camera.setMatrix();

        if (!gameEnded) { // pokud hra běží -> zapnu světlo, mlhu
            glEnable(GL_FOG);
            float[] light_position;
            light_position = new float[]{(float) camera.getPosition().getX(), (float) camera.getPosition().getY(), (float) camera.getPosition().getZ(), 1.0f}; //bod světla
            glLightfv(GL_LIGHT0, GL_POSITION, light_position);
            float[] light_direction = {(float) camera.getEyeVector().getX(), (float) camera.getEyeVector().getY(), (float) camera.getEyeVector().getZ(), 0f}; //směr reflektoru
            glEnable(GL_LIGHTING); // stínování
            glEnable(GL_LIGHT0);
            glShadeModel(GL_SMOOTH);
            glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 80);
            glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light_direction);
            glEnable(GL_COLOR_MATERIAL);
        }
        renderScene(); // render herních objektů
        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        endTime = System.currentTimeMillis(); // čas pro výpočet aktuálního času v labyrintu
        if (!gameEnded) { // pokud hra neskončila -> počítám aktuální čas, skóre
            if (collectedScore != maxScoreGenerated) {
                scoreInfo = "Posbirane score: " + collectedScore + " | Max: " + maxScoreGenerated + " | Letani: " + flightMode;
                estimatedTime = (endTime - startTime) / 1000;
            } else { // pokud posbírám všechny body, ukážu souřadnice cíle a kamery
                scoreInfo = "Byl otevren vychod! Souřadnice X " + mg.getEnd().getX() + ", Z " + mg.getEnd().getZ() + " | Tvé souřadnice: X " + camera.getPosition().getX() +
                        ", Z " + camera.getPosition().getZ();
            }
        } else { // dokončení hry - volný pohyb, další data nejsou potřeba
            scoreInfo = "Dohrano, dostal jsi volnou kameru!";
        }

        // informativní text
        textRenderer.addStr2D(3, 20, scoreInfo);
        textRenderer.addStr2D(3, 40, "Čas strávený v labyrintu: " + estimatedTime + "s");
        String helpInfo = "Stiskni H pro dialogove okno s napovedou.";
        textRenderer.addStr2D(5, height - 5, helpInfo);

    }

    private void renderScene() {
        glEnable(GL_NORMALIZE); //light
        setMaterial();
        glPushMatrix();

        for (Score sc : mp.getScoreList()) { // vykreslení skóre
            if (renderInRadius) { // rozhoduji zda je potřeba vykreslit jen objekty v radiusu
                if (sc.isNearCamera(camera, 15)) { // vykresluji skóre v radiusu - 15 optimální (kvůli rovinkám a leteckému módu)
                    sc.renderScore(scoreTexture);
                }
            } else {
                sc.renderScore(scoreTexture);
            }
        }


        for (Wall wall : mp.getWallList()) { // vykreslení zdí
            if (renderInRadius) { // rozhoduji zda je potřeba vykreslit jen objekty v radiusu
                if (wall.isNearCamera(camera, 15)) { // vykresluji zdi v radiusu - 15 optimální (kvůli rovinkám a leteckému módu)
                    wall.renderWall(wallTexture);
                }
            } else {
                wall.renderWall(wallTexture);
            }
        }
        fl.renderFloor(floorTexture); // vykreslení podlahy

        startPoint.renderFloorPoint(startTexture); // vykreslení bodů start a konec
        endPoint.renderFloorPoint(endTexture);
        glPopMatrix();
    }

    private void moveCameraFPS() { //pohyb dle fps - plynuléé
        if (!flightMode) { // pokud není zapnut lítací mód

            if (!gameEnded) { // pokud hra neskončila
                switch (cameraPressKey) { // vlastní pohyb kamerou - bez zenitu (aby nešlo létat v klasickém módu)
//forward
                    case 1 -> cameraAction("forward");
//backward
                    case 2 -> cameraAction("back");
//left
                    case 3 -> cameraAction("left");
//right
                    case 4 -> cameraAction("right");
                }
            } else {
                switch (cameraPressKey) { // volný pohyb
                    case 1:
                        camera.forward(deltaTrans + 0.1);
                        break;
                    case 2:
                        camera.backward(deltaTrans + 0.1);
                        break;
                    case 3:
                        camera.left(deltaTrans + 0.1);
                        break;
                    case 4:
                        camera.right(deltaTrans + 0.1);
                        break;
                    default: // no key pressed
                        break;
                }
            }
        }

    }

    public void cameraAction(String dir) { // provede pohyb bez zenitu a pak vrátí zenit kvůli pohledu
        zenith = camera.getZenith();
        setCameraWithoutZenith(dir, camera);
        camera.setZenith(zenith);
    }

    private void setCameraWithoutZenith(String dir, GLCamera cam) {
        cam.setZenith(0);
        switch (dir) {
            case "forward" -> {
                cam.forward(deltaTrans); // pohnu se dopředu
                if (mg.getWalls()[(int) cam.getPosition().getX()][(int) cam.getPosition().getZ()]) { // zjišťuji zda není kolize
                    cam.backward(deltaTrans); // pohnu se zpět 2x
                    cam.backward(deltaTrans);
                }
            }
            // to samé jako forward akorát v jiném směru
            case "back" -> {
                cam.backward(deltaTrans);
                if (mg.getWalls()[(int) cam.getPosition().getX()][(int) cam.getPosition().getZ()]) {
                    cam.forward(deltaTrans);
                    cam.forward(deltaTrans);
                }
            }
            case "left" -> {
                cam.left(deltaTrans);
                if (mg.getWalls()[(int) cam.getPosition().getX()][(int) cam.getPosition().getZ()]) {
                    cam.right(deltaTrans);
                    cam.right(deltaTrans);
                }
            }
            case "right" -> {
                cam.right(deltaTrans);
                if (mg.getWalls()[(int) cam.getPosition().getX()][(int) cam.getPosition().getZ()]) {
                    cam.left(deltaTrans);
                    cam.left(deltaTrans);
                }
            }
            default -> //chyba - neplatný směr - nemělo by nastat?
                    System.out.println("Neplatný směr: " + dir);
        }
        if (walkDown) { // animace pohybu + zvuk
            playerHeight = playerHeight - 0.005f;
            cam.setPosition(cam.getPosition().withY(playerHeight));
            if (playerHeight <= 1.3f) {
                walkDown = false;
                makeSound(this.getClass().getResource("/sounds/walk.wav"));
            }
        } else {
            playerHeight = playerHeight + 0.005f;
            cam.setPosition(cam.getPosition().withY(playerHeight));
            if (playerHeight >= 1.5) {
                walkDown = true;
                makeSound(this.getClass().getResource("/sounds/walk.wav"));
            }
        }

        checkForEnd(cam); // kontroluji zda nejsem na konci
        checkForScore(cam); // kontroluji zda nestojím ve skóre
    }

    private void checkForEnd(GLCamera cam) {
        if (endPoint.isNearCamera(cam, 0.5f) && collectedScore == maxScoreGenerated) { // pokud stojím v konci a mám všechno skóre -> konec a konečný mód
            makeSound(this.getClass().getResource("/sounds/winner.wav"));
            gameEnded = !gameEnded;
            renderInRadius = !renderInRadius;
            glDisable(GL_FOG);
        }
    }

    private void checkForScore(GLCamera cam) { // kontrola zda stojím ve skóre - pokud ano přičtu do proměnné a přehraji zvuk
        for (Score sc : mp.getScoreList()) {
            if (sc.isNearCamera(cam, 0.5f)) {
                collectedScore++;
                mp.getScoreList().remove(sc);
                makeSound(this.getClass().getResource("/sounds/scorePickup.wav"));
                break;
            }
        }
    }

    public void makeSound(URL sound) { // metoda na přehrávání zvuku dle url
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-7.0f); // Ztlumení - v původní verzi byli zvuky příliš hlasité
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void settings() { // nastavení parametrů pro labyrint
        int n = JOptionPane.showConfirmDialog(
                null,
                "Chceš nagenerovat náhodnou velikost bludiště?",
                "Nastavení bludiště",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) { // automatické generování velikosti
            mazeSize = -1;
        } else if (n == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        } else { // manuální generování velikosti
            String s = JOptionPane.showInputDialog(null, "Zvol velikost bludiště v intervalu 10-40", "Nastavení bludiště", JOptionPane.QUESTION_MESSAGE);
            try { // kontrola inputu
                mazeSize = Integer.parseInt(s);
                if (!(mazeSize >= 10 && mazeSize <= 40)) {
                    JOptionPane.showMessageDialog(null, "Je potřeba zvolit číslo v rozsahu!");
                    settings();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Je potřeba zvolit platné číslo!");
                settings();
            }
        }
    }

    //část s osvětlením ->
    private void setLight() {
        // light source setting - specular component
        float[] light_spec = new float[]{1, 1, 1, 1};
        // light source setting - diffuse component
        float[] light_dif = new float[]{1, 1, 1, 1};
        // light source setting - ambient component
        float[] light_amb = new float[]{1, 1, 1, 1};

        glLightfv(GL_LIGHT0, GL_AMBIENT, light_amb);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, light_dif);
        glLightfv(GL_LIGHT0, GL_SPECULAR, light_spec);
        glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, kH);
    }

    private void setMaterial() {
        float initV = 0;
        // surface material setting - specular reflection
        float[] mat_spec = new float[]{initV, initV, initV, 1};
        // surface material setting - diffuse reflection
        float[] mat_dif = new float[]{initV, initV, initV, 1};
        // surface material setting - ambient reflection
        float[] mat_amb = new float[]{initV, initV, initV, 1};

        // surface material setting - emission
        float[] mat_emis = new float[]{initV, initV, initV, 1};

        glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, mat_amb);
        glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, mat_dif);
        glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, mat_spec);
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, kH);
        glMaterialfv(GL_FRONT, GL_EMISSION, mat_emis);
    }
    // konec osvětlení
}
