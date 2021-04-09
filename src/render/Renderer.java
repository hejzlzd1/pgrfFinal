package render;

import extension.global.AbstractRenderer;
import extension.global.GLCamera;
import models.Floor;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.Point3D;
import utils.MazeGenerator;
import utils.MazeProcessor;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static extension.global.GluUtils.gluPerspective;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class Renderer extends AbstractRenderer {
    private float dx, dy, ox, oy;
    private float zenit, azimut;
    private lwjglutils.OGLTexture2D floorTexture;
    private lwjglutils.OGLTexture2D wallTexture;
    private lwjglutils.OGLTexture2D scoreTexture;
    private double zenith;
    private GLCamera camera;
    private MazeGenerator mg;
    private boolean gameEnded = true;
    private float deltaTrans = 0;
    private boolean mouseButton1 = false;

    public Renderer() {
        super();

        /*used default glfwWindowSizeCallback see AbstractRenderer*/

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_RELEASE) {
                    deltaTrans = 0;
                }

                if (action == GLFW_PRESS) {
                    switch (key) {

                        case GLFW_KEY_W:
                        case GLFW_KEY_S:
                        case GLFW_KEY_A:
                        case GLFW_KEY_D:
                            deltaTrans = 0.1f;
                            break;
                    }
                }
                switch (key) {
                    case GLFW_KEY_W:
                        if(gameEnded){
                            cameraAction("forward");
                        }else{
                            camera.forward(deltaTrans+0.2);
                        }
                        break;
                    case GLFW_KEY_S:
                        if(gameEnded) {
                            cameraAction("back");
                        }else{
                                camera.backward(deltaTrans+0.2);
                            }
                        break;
                    case GLFW_KEY_A:
                        if(gameEnded){
                            cameraAction("left");
                        }else{
                             camera.left(deltaTrans+0.2);
                         }
                        break;
                    case GLFW_KEY_D:
                        if(gameEnded) {
                            cameraAction("right");
                        }else{
                            camera.right(deltaTrans+0.2);
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
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        mg = new MazeGenerator();
        mg.generateMaze(50);

        camera = new GLCamera();
        camera.setPosition(new transforms.Vec3D(mg.getStart().getX()+0.5,1.5,mg.getStart().getZ()+0.5));
        camera.setFirstPerson(true);

        try {
            floorTexture = new lwjglutils.OGLTexture2D("textures/stonefloor.jpg");
            wallTexture = new lwjglutils.OGLTexture2D("textures/wall.jpg");
            scoreTexture = new lwjglutils.OGLTexture2D("textures/score.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glEnable(GL_DEPTH_TEST);
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glEnable(GL_FOG);
        glFogi(GL_FOG_MODE, GL_LINEAR);
        glFogi(GL_FOG_START, (int) 0f);
        glFogi(GL_FOG_END, (int) 10f);
        glFogf(GL_FOG_DENSITY, 0.08f);
        glFogfv(GL_FOG_COLOR, new float[]{0.1f, 0.1f, 0.1f, 1});

        gluPerspective(45, width / (float) height, 0.01f, 500.0f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glPushMatrix();
        camera.setMatrix();
        MazeProcessor.createWalls(mg.getWalls(),wallTexture);
        glPopMatrix();

        glPushMatrix();
        camera.setMatrix();
        MazeProcessor.createScore(mg.getWalls(),15,scoreTexture);
        glPopMatrix();

        glPushMatrix();
        camera.setMatrix();
        new Floor(new Point3D(0,0,0),1f,mg.getWidth()+1,mg.getHeight()+1,floorTexture); //maze width/height
        glPopMatrix();

        String textInfo = "position " + camera.getPosition().toString();
        textRenderer.addStr2D(3, 40, textInfo);
    }

    public void cameraAction(String dir) {
        zenith = camera.getZenith();
        setCameraWithoutZenith(dir, camera);
        camera.setZenith(zenith);
    }

    private void setCameraWithoutZenith(String dir, GLCamera cam) {
        cam.setZenith(0);
        switch(dir){
            case "forward":
                cam.forward(deltaTrans);
                if(mg.getWalls()[(int)cam.getPosition().getX()][(int)cam.getPosition().getZ()]){
                    cam.backward(deltaTrans);
                }
                break;
            case "back":
                cam.backward(deltaTrans);
                if(mg.getWalls()[(int)cam.getPosition().getX()][(int)cam.getPosition().getZ()]){
                    cam.forward(deltaTrans);
                }
                break;
            case "left":
                cam.left(deltaTrans);
                if(mg.getWalls()[(int)cam.getPosition().getX()][(int)cam.getPosition().getZ()]){
                    cam.right(deltaTrans);
                }
                break;
            case "right":
                cam.right(deltaTrans);
                if(mg.getWalls()[(int)cam.getPosition().getX()][(int)cam.getPosition().getZ()]){
                    cam.left(deltaTrans);
                }
                break;
        }
    }

}
