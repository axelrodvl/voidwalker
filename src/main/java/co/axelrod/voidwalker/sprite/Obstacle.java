package co.axelrod.voidwalker.sprite;

import co.axelrod.voidwalker.model.Drawable;
import co.axelrod.voidwalker.model.HitBox;
import co.axelrod.voidwalker.model.Position;
import co.axelrod.voidwalker.model.feature.Collidable;
import co.axelrod.voidwalker.model.feature.Destroyable;
import co.axelrod.voidwalker.model.feature.Interactable;
import co.axelrod.voidwalker.model.feature.Movable;
import co.axelrod.voidwalker.model.game.State;

import java.awt.*;
import java.util.Random;

import static co.axelrod.voidwalker.config.HealthConfiguration.OBSTACLE_HP;

public class Obstacle extends Drawable implements Interactable, Collidable, Destroyable, Movable {
    private static final int VELOCITY_X = 1;

    private int hp = OBSTACLE_HP;

    private final Position initialPosition;

    private final Player player;
    private final State state;

    private boolean scored = false;

    public static Obstacle getObstacle(String name, int x, Player player, State state) {
        int height = new Random().nextInt(50);
        Position obstaclePosition = new Position(x, state.floor - height);
        HitBox obstacleHitBox = new HitBox(50 + new Random().nextInt(50), height);

        return new Obstacle(name, obstaclePosition, obstacleHitBox, player, state);
    }

    public Obstacle(String name, Position position, HitBox hitBox, Player player, State state) {
        super(name, position, hitBox);
        this.initialPosition = new Position(position.x, position.y);
        this.player = player;
        this.state = state;
    }

    @Override
    public void actionPerformed() {
        position.setX(position.getX() - getVelocityX());
        if (position.getX() < -1 * hitBox.getWidth()) {
            resetMotion();
        }

        if (!scored && player.position.x > position.x + hitBox.getWidth()) {
            state.setScore(state.getScore() + 1);
            scored = true;
        }
    }

    public void resetMotion() {
        scored = false;

        int height = new Random().nextInt(50);
        Position obstaclePosition = new Position(state.width, state.floor - height);
        HitBox obstacleHitBox = new HitBox(50 + new Random().nextInt(50), height);

        position = obstaclePosition;
        hitBox = obstacleHitBox;
    }

    @Override
    public void reset() {
        scored = false;

        int height = new Random().nextInt(50);
        Position obstaclePosition = new Position(initialPosition.x, state.floor - height);
        HitBox obstacleHitBox = new HitBox(50 + new Random().nextInt(50), height);

        position = obstaclePosition;
        hitBox = obstacleHitBox;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(position.x, position.y, hitBox.getWidth(), hitBox.getHeight());
    }

    @Override
    public int hp() {
        return hp;
    }

    @Override
    public void damage(int damage) {
        hp -= damage;
        if (readyToDestroy()) {
            state.setScore(state.getScore() + 1);
        }
    }

    @Override
    public boolean readyToDestroy() {
        return hp <= 0;
    }

    @Override
    public int getVelocityX() {
        return VELOCITY_X * state.getVelocityFactor();
    }

    @Override
    public int getVelocityY() {
        return 0;
    }
}
