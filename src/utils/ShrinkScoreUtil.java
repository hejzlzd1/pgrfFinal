package utils;

import extension.global.GLCamera;
import models.Score;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShrinkScoreUtil {
    private GLCamera camera;

    public void initTimerForScoreSize(MazeProcessor mp) {
        Runnable scoreShrink = () -> {
            if (!mp.getScoreList().isEmpty()) {
                for (Score sc : mp.getScoreList()) {
                    if (sc.isNearCamera(camera, 3) && sc.isShouldShrink()) {
                        sc.setSize(sc.getSize() - 0.01f);
                        if (sc.getSize() <= 0.1f) sc.setShouldShrink(false);
                    }
                    if (sc.isNearCamera(camera, 3) && !sc.isShouldShrink()) {
                        sc.setSize(sc.getSize() + 0.01f);
                        if (sc.getSize() == 0.2f) sc.setShouldShrink(true);
                    }
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(scoreShrink, 200, 100, TimeUnit.MILLISECONDS);
    }

    public void setCamera(GLCamera camera) {
        this.camera = camera;
    }
}
