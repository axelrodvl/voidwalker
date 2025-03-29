package co.axelrod.voidwalker.sprite;

import co.axelrod.voidwalker.config.KeybindingConfiguration;
import co.axelrod.voidwalker.model.Drawable;
import co.axelrod.voidwalker.model.feature.*;
import co.axelrod.voidwalker.model.HitBox;
import co.axelrod.voidwalker.model.Position;
import co.axelrod.voidwalker.model.game.State;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import static co.axelrod.voidwalker.config.HealthConfiguration.BULLET_HP;

public class Bullet extends Drawable implements Playable, Interactable, Destroyable, Collidable, Movable {
    private static final int VELOCITY_X = 1;

    private final Player player;

    private boolean hit = false;
    private int hp = BULLET_HP;

    private final State state;

    private BufferedImage image = createImage();

    public Bullet(String name, Player player, State state) {
        super(name, new Position(player.position.x + 25, player.position.y - player.hitBox.getHeight() / 2), new HitBox(5, 5));
        this.player = player;
        this.state = state;
    }

    public void actionPerformed() {
        if (hit) {
            return;
        }
        position.setX(position.getX() + getVelocityX());
        if (position.getX() > state.width) {
            hit = true;
        }
    }

    @Override
    public void reset() {
        hit = true;

        hp = BULLET_HP;
        position.setX(player.position.x);
        position.setY(player.position.y);
        hitBox.setWidth(5);
        hitBox.setHeight(5);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (hit) {
            return;
        }

        g.drawImage(image, position.x, position.y, null);
    }

    @Override
    public KeyListener keyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeybindingConfiguration.FIRE_KEY && !hit) {
                    hit = true;
                }
            }
        };
    }

    private BufferedImage createImage() {
        BufferedImage image = new BufferedImage(hitBox.getWidth(), hitBox.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.RED);
        g2.fillOval(0, 0, hitBox.getWidth(), hitBox.getHeight());

        g2.dispose();
        return image;
    }

    @Override
    public int hp() {
        return hp;
    }

    @Override
    public void damage(int damage) {
        hp -= damage;
    }

    @Override
    public boolean readyToDestroy() {
        return hit || hp <= 0;
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
