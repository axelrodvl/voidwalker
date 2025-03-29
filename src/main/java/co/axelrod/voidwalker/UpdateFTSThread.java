package co.axelrod.voidwalker;

import java.util.concurrent.TimeUnit;

import static co.axelrod.voidwalker.model.game.State.TARGET_FTS;

public class UpdateFTSThread implements Runnable {
    private final Game game;

    public UpdateFTSThread(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        long targetTime = 1000 / TARGET_FTS;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();

        int steps = 0;

        while (game.running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            double sleepTime = targetTime - deltaTime;
            lastTime = now;

            game.update();
            steps++;

            try {
                TimeUnit.MILLISECONDS.sleep((long) sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                game.fts = steps;
                steps = 0;
            }
        }
    }
}
