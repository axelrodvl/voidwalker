package co.axelrod.voidwalker;

import co.axelrod.voidwalker.config.HealthConfiguration;
import co.axelrod.voidwalker.config.KeybindingConfiguration;
import co.axelrod.voidwalker.model.Drawable;
import co.axelrod.voidwalker.model.HitBox;
import co.axelrod.voidwalker.model.Position;
import co.axelrod.voidwalker.model.feature.Destroyable;
import co.axelrod.voidwalker.model.feature.Interactable;
import co.axelrod.voidwalker.model.feature.Playable;
import co.axelrod.voidwalker.model.game.State;
import co.axelrod.voidwalker.sprite.*;
import co.axelrod.voidwalker.tasks.CollisionDetection;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class Game extends Canvas {
    public boolean running = true;
    public int fps = 0;
    public int fts = 0;

    private java.util.List<Drawable> drawables = new CopyOnWriteArrayList<>();

    private final State state = new State();

    private final Player player;

    private final CollisionDetection collisionDetection;

    public Game() {
        Frame frame = new Frame(State.MESSAGES.getString("gameName"));
        frame.setSize(state.width, state.height);
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setFocusable(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
                System.exit(0);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                log.info("New size: " + getSize());
                state.width = getSize().width;
                state.height = getSize().height;
                frame.requestFocusInWindow();
            }
        });
        frame.addKeyListener(gameOverKeyListener());

        createBufferStrategy(2); // Двойная буферизация

        player = new Player(state, "Player", new Position(50, state.floor - 50), new HitBox(20, 50));

        drawables.add(new FractalTree("Tree 1", new Position(state.width / 5, state.height), new HitBox(200, 150), 50));
        drawables.add(new FractalTree("Tree 2", new Position(state.width / 2, state.height), new HitBox(150, 150), 70));
        drawables.add(new FractalTree("Tree 3", new Position(state.width / 3 * 2 + 50, state.height), new HitBox(150, 150), 40));
        drawables.add(new FractalTree("Tree 4", new Position(state.width - 70, state.height), new HitBox(150, 150), 30));

        drawables.add(player);
        drawables.add(Obstacle.getObstacle("Obstacle 1", 800, player, state));
        drawables.add(Obstacle.getObstacle("Obstacle 2", 1000, player, state));
        drawables.add(Obstacle.getObstacle("Obstacle 3", 1200, player, state));

        drawables.stream()
                .filter(Playable.class::isInstance)
                .map(x -> ((Playable) x).keyListener())
                .forEach(frame::addKeyListener);

        collisionDetection = new CollisionDetection(drawables);

        state.setVelocityFactor(5);
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        Graphics g = bs.getDrawGraphics();
        g.setColor(new Color(0x87CEEB));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawString("FPS: " + fps, 500, 20);
        g.drawString("FTS: " + fts, 500, 40);

        g.setColor(Color.GREEN);
        g.fillRect(0, state.floor, getWidth(), 50);

        drawables.forEach(drawableObject -> drawableObject.paintComponent(g));

        printStat(g);

        g.drawString(State.MESSAGES.getString("health") + ": " + player.getHp(), 300, 20);
        g.drawString(State.MESSAGES.getString("score") + ": " + state.getScore(), 300, 40);
        g.drawString(State.MESSAGES.getString("controlsHint"), 300, 60);

        if (state.isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(State.MESSAGES.getString("gameOver"), state.width / 2 - 50, state.height / 2 - 20);
            g.drawString(State.MESSAGES.getString("pressRestart"), state.width / 2 - 150, state.height / 2 + 20);
        }

        g.dispose();
        bs.show();
    }

    private void printStat(Graphics g) {
        g.setColor(Color.BLACK);

        int y = 20;
        int offset = 20;
        for (Drawable drawable : drawables) {
            g.drawString(drawable.name + ": " + drawable.position.x + ", " + drawable.position.y, 20, y);
            y += offset;
        }
    }

    private KeyAdapter gameOverKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (state.isGameOver()) {
                    if (e.getKeyCode() == KeybindingConfiguration.RESTART_KEY) {
                        drawables.forEach(Drawable::reset);
                        state.setScore(0);
                        state.setGameOver(false);
                    }
                } else {
                    if (e.getKeyCode() == KeybindingConfiguration.FIRE_KEY) {
                        drawables.add(new Bullet("Bullet", player, state));
                    }
                }
            }
        };
    }

    public void update() {
        if (state.isGameOver()) {
            render();
            return;
        } else {
            collisionDetection.detectCollisions();
        }

        if (player.readyToDestroy()) {
            state.setGameOver(true);
            return;
        }

        java.util.List<Drawable> toDestroyList = new ArrayList<>();
        for (Drawable drawable : drawables) {
            if (drawable instanceof Destroyable destroyable && destroyable.readyToDestroy()) {
                toDestroyList.add(drawable);
            }
        }

        for (Drawable drawable : toDestroyList) {
            drawables.remove(drawable);
        }

        if (drawables.stream().noneMatch(Obstacle.class::isInstance)) {
            drawables.add(Obstacle.getObstacle("Obstacle 1", 800, player, state));
            drawables.add(Obstacle.getObstacle("Obstacle 2", 1000, player, state));
            drawables.add(Obstacle.getObstacle("Obstacle 3", 1200, player, state));
        }

        if (player.getHp() < HealthConfiguration.PLAYER_HP / 3) {
            if (drawables.stream().noneMatch(Heart.class::isInstance)) {
                drawables.add(new Heart("Heart", player, state));
            }
        }

        drawables.stream()
                .filter(Interactable.class::isInstance)
                .forEach(drawableObject -> ((Interactable) drawableObject).actionPerformed());
    }
}
