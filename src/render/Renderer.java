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
import org.lwjgl.glfw.GLFWScrollCallback;
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
    private long startTime, endTime;


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
                        case GLFW_KEY_W, GLFW_KEY_D, GLFW_KEY_S, GLFW_KEY_A -> deltaTrans = 0.05f;
                        case GLFW_KEY_M -> {
                            if (flightMode) {
                                camera.setPosition(camera.getPosition().withY(1.5));
                            }
                            flightMode = !flightMode;
                        }
//debug mode
                        case GLFW_KEY_F1 -> {
                            gameEnded = !gameEnded;
                            renderInRadius = !renderInRadius;
                            glDisable(GL_FOG);
                            camera.setPosition(new Vec3D(mg.getStart().getX() + 0.5, 1.5, mg.getStart().getZ() + 0.5));
                        }
                        case GLFW_KEY_H -> JOptionPane.showMessageDialog(null, "H - Toto okno \n WSAD - Pohyb \n M - Přepínání mezi pohybem a levitací pohledem \n R - Let nahoru \n F - Let dolu \n C - reset \n F1 - debug mód (dočasné - zpřístupní mód po dokončení labyrintu)");
                        case GLFW_KEY_C -> init();
                    }
                }
                switch (key) {
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
                    case GLFW_KEY_R:
                        if (flightMode && camera.getPosition().getY() < 6)
                            camera.setPosition(camera.getPosition().withY(camera.getPosition().getY() + deltaTrans));
                        break;
                    case GLFW_KEY_F:
                        if (flightMode && camera.getPosition().getY() > 1.4) camera.down(deltaTrans);
                        break;

                }
                if (action == GLFW_RELEASE) {
                    switch (key) {
                        case GLFW_KEY_W, GLFW_KEY_S, GLFW_KEY_A, GLFW_KEY_D -> cameraPressKey = 0;
                    }
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
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
            public void invoke(long window, double x, double y) {
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

        glfwScrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                //do nothing
            }
        };
    }


    @Override
    public void init() {
        super.init();
        settings();
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        setFog();

        cameraPressKey = 0;
        deltaTrans = 0.05f;
        gameEnded = false;
        flightMode = false;
        renderInRadius = true;

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        ssu = new ShrinkScoreUtil();
        mp = new MazeProcessor();
        ssu.initTimerForScoreSize(mp);

        mg = new MazeGenerator();
        if (mazeSize == -1) {
            mazeSize = new Random().nextInt(40 + 1 - 10) + 10;
        }

        mg.generateMaze(mazeSize);

        startTime = System.currentTimeMillis();
        maxScoreGenerated = new Random().nextInt(mazeSize + 1 - 1) + 1;
        collectedScore = 0;
        mp.createScore(mg.getWalls(), maxScoreGenerated);
        mp.createWalls(mg.getWalls(), wallTexture);
        startPoint = new FloorPoints(mg.getStart());
        endPoint = new FloorPoints(mg.getEnd());
        fl = new Floor(new Point3D(0, 0, 0), 1f, mg.getWidth() + 1, mg.getHeight() + 1);

        camera = new GLCamera();
        camera.setPosition(new transforms.Vec3D(mg.getStart().getX() + 0.5, 1.5, mg.getStart().getZ() + 0.5));
        camera.setFirstPerson(true);
        playerHeight = 1.5f;
        walkDown = true;

        setLight();

        try {
            floorTexture = new lwjglutils.OGLTexture2D("textures/stonefloor.jpg");
            wallTexture = new lwjglutils.OGLTexture2D("textures/wall.jpg");
            scoreTexture = new lwjglutils.OGLTexture2D("textures/score.jpg");
            endTexture = new lwjglutils.OGLTexture2D("textures/endHole.png");
            startTexture = new lwjglutils.OGLTexture2D("textures/start.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFog() {
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

        setLight();
        moveCameraFPS(); // smooth camera - because movement is with every frame (not by press)
        ssu.setCamera(camera);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        gluPerspective(70, width / (float) height, 0.01f, 500.0f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        camera.setMatrix();
        if (!gameEnded) {
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
        renderScene();
        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        endTime = System.currentTimeMillis();
        if (!gameEnded) {
            if (collectedScore != maxScoreGenerated) {
                scoreInfo = "Posbirane score: " + collectedScore + " | Max: " + maxScoreGenerated + " | Letani: " + flightMode;
            } else {
                scoreInfo = "Byl otevren vychod! Souřadnice X " + mg.getEnd().getX() + ", Z " + mg.getEnd().getZ() + " | Tvé souřadnice: X " + camera.getPosition().getX() +
                        ", Z " + camera.getPosition().getZ();
            }
        } else {
            scoreInfo = "Dohrano, dostal jsi volnou kameru!";
        }

        textRenderer.addStr2D(3, 20, scoreInfo);
        textRenderer.addStr2D(3, 40, "Čas strávený v labyrintu: " + (endTime - startTime) / 1000 + "s");
        String helpInfo = "Stiskni H pro dialogove okno s napovedou.";
        textRenderer.addStr2D(5, height - 5, helpInfo);

    }

    private void renderScene() {
        glEnable(GL_NORMALIZE); //light
        setMaterial();
        glPushMatrix();

        for (Score sc : mp.getScoreList()) {
            if (renderInRadius) {
                if (sc.isNearCamera(camera, 15)) {

                    sc.renderScore(scoreTexture);
                }
            } else {
                sc.renderScore(scoreTexture);
            }
        }
        glPopMatrix();

        setMaterial();
        glPushMatrix();

        for (Wall wall : mp.getWallList()) {
            if (renderInRadius) {
                if (wall.isNearCamera(camera, 15)) { // optimalization, rendering just in radius (if game is not complete)
                    wall.renderWall(wallTexture);
                }
            } else {
                wall.renderWall(wallTexture);
            }
        }

        glPopMatrix();

        setMaterial();
        glPushMatrix();

        fl.renderFloor(floorTexture);
        glPopMatrix();

        glPushMatrix();

        setMaterial();
        startPoint.renderFloorPoint(startTexture);
        endPoint.renderFloorPoint(endTexture);
        glPopMatrix();
    }

    private void moveCameraFPS() {
        if (!flightMode) {

            if (!gameEnded) {
                switch (cameraPressKey) {
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
                switch (cameraPressKey) {
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

    public void cameraAction(String dir) {
        zenith = camera.getZenith();
        setCameraWithoutZenith(dir, camera);
        camera.setZenith(zenith);
    }

    private void setCameraWithoutZenith(String dir, GLCamera cam) {
        cam.setZenith(0);
        switch (dir) {
            case "forward" -> {
                cam.forward(deltaTrans);
                if (mg.getWalls()[(int) cam.getPosition().getX()][(int) cam.getPosition().getZ()]) {
                    cam.backward(deltaTrans);
                    cam.backward(deltaTrans);
                }
            }
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
            default -> //chyba - neplatný směr?
                    System.out.println("Neplatný směr: " + dir);
        }
        if (walkDown) {
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

        checkForEnd(cam);
        checkForScore(cam);
    }

    private void checkForEnd(GLCamera cam) {
        if (endPoint.isNearCamera(cam, 0.5f) && collectedScore == maxScoreGenerated) {
            makeSound(this.getClass().getResource("/sounds/winner.wav"));
            gameEnded = !gameEnded;
            renderInRadius = !renderInRadius;
            glDisable(GL_FOG);
        }
    }

    private void checkForScore(GLCamera cam) {
        for (Score sc : mp.getScoreList()) {
            if (sc.checkPositionWithCam(cam)) {
                collectedScore++;
                mp.getScoreList().remove(sc);
                makeSound(this.getClass().getResource("/sounds/scorePickup.wav"));
                break;
            }
        }
    }

    public void makeSound(URL sound) {
        System.out.println(sound);
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-7.0f); // Reduce volume by 10 decibels.
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void settings() {
        int n = JOptionPane.showConfirmDialog(
                null,
                "Chceš nagenerovat náhodnou velikost bludiště?",
                "Nastavení bludiště",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            mazeSize = -1;
        } else {
            String s = JOptionPane.showInputDialog(null, "Zvol velikost bludiště v intervalu 10-40", "Nastavení bludiště", JOptionPane.QUESTION_MESSAGE);
            try {
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

    //lightning
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

    private void setMaterial(int mode) {
        float initV = 0;
        // surface material setting - specular reflection
        float[] mat_spec = new float[]{initV, initV, initV, 1};
        // surface material setting - diffuse reflection
        float[] mat_dif = new float[]{initV, initV, initV, 1};
        // surface material setting - ambient reflection
        float[] mat_amb = new float[]{initV, initV, initV, 1};

        // surface material setting - emission
        float[] mat_emis = new float[]{initV, initV, initV, 1};

        int index = mode % 3;

        mat_dif[index] = kD;
        mat_spec[index] = kS;
        mat_amb[index] = kA;
        mat_emis[index] = kE;

        glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, mat_amb);
        glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, mat_dif);
        glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, mat_spec);
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, kH);
        glMaterialfv(GL_FRONT, GL_EMISSION, mat_emis);
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
}
