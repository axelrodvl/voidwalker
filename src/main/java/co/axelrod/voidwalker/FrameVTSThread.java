package co.axelrod.voidwalker;

import java.util.concurrent.TimeUnit;

import static co.axelrod.voidwalker.model.game.State.FRAME_LIMIT;
import static co.axelrod.voidwalker.model.game.State.TARGET_FPS;

public class FrameVTSThread implements Runnable {
    private final Game game;

    public FrameVTSThread(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        long targetTime = 1000 / TARGET_FPS;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        int frames = 0;

        while (game.running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            double sleepTime = targetTime - deltaTime;
            lastTime = now;

            game.render();

            if (FRAME_LIMIT) {
                try {
                    TimeUnit.MILLISECONDS.sleep((long) sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                game.fps = frames;
                frames = 0;
            }
        }
    }
}
