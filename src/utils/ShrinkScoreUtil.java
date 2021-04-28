package utils;

import extension.global.GLCamera;
import models.Score;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShrinkScoreUtil {
    private GLCamera camera;

    public void initTimerForScoreSize(MazeProcessor mp) { // odstartuje animace v novém vlákně
        Runnable scoreShrink = () -> {
            if (!mp.getScoreList().isEmpty()) { // pokud existuje nějaké skóre na mapě
                for (Score sc : mp.getScoreList()) {
                    if (sc.isNearCamera(camera, 8)) { // pokud je skóre v radiusu (8 "pozic" okolo kamery)
                        if (sc.isShouldShrink()) { // rozhodování o dalším kroku animace - zmenšit/zvětšit
                            sc.setSize(sc.getSize() - 0.01f);
                            if (sc.getSize() <= 0.1f) sc.setShouldShrink(false);
                        } else {
                            sc.setSize(sc.getSize() + 0.01f);
                            if (sc.getSize() == 0.2f) sc.setShouldShrink(true);
                        }
                        if (sc.isShouldFlyDown()) { // // rozhodování o dalším kroku animace - letět nahoru/dolu
                            sc.setPosition(sc.getPosition().withY(sc.getPosition().getY() - 0.01f));
                            if (sc.getPosition().getY() <= 0.8f) sc.setShouldFlyDown(false);
                        } else {
                            sc.setPosition(sc.getPosition().withY(sc.getPosition().getY() + 0.01f));
                            if (sc.getPosition().getY() >= 1.2f) sc.setShouldFlyDown(true);
                        }
                    }
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(scoreShrink, 200, 100, TimeUnit.MILLISECONDS); // spouštění animace každých 100 ms
    }

    public void setCamera(GLCamera camera) {
        this.camera = camera;
    }
}
