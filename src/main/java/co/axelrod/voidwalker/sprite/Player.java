package co.axelrod.voidwalker.sprite;

import co.axelrod.voidwalker.config.KeybindingConfiguration;
import co.axelrod.voidwalker.model.Drawable;
import co.axelrod.voidwalker.model.feature.*;
import co.axelrod.voidwalker.model.HitBox;
import co.axelrod.voidwalker.model.Position;
import co.axelrod.voidwalker.model.game.State;
import lombok.Getter;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import static co.axelrod.voidwalker.config.HealthConfiguration.PLAYER_HP;

@Getter
public class Player extends Drawable implements Playable, Interactable, Collidable, Destroyable, Movable {
    private static final boolean UNDER_DAMAGE = true;

    private static final int VELOCITY_X = 1;
    private static final int VELOCITY_Y = 15;

    private int hp = PLAYER_HP;

    private State state;

    private BufferedImage playerImageUnderDamage = createPlayerImage(UNDER_DAMAGE, PLAYER_HP);
    private BufferedImage playerImageHealthHigh = createPlayerImage(!UNDER_DAMAGE, (int) (PLAYER_HP * 0.9));
    private BufferedImage playerImageHealthMedium = createPlayerImage(!UNDER_DAMAGE, (int) (PLAYER_HP * 0.5));
    private BufferedImage playerImageHealthLow = createPlayerImage(!UNDER_DAMAGE, (int) (PLAYER_HP * 0.1));

    private int velX;
    private int velY;

    private boolean jumping = false;
    private boolean damaged = false;

    public Player(State state, String name, Position position, HitBox hitBox) {
        super(name, position, hitBox);
        this.state = state;
    }

    @Override
    public KeyListener keyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeybindingConfiguration.LEFT_KEY) {
                    velX = -getVelocityX();
                } else if (e.getKeyCode() == KeybindingConfiguration.RIGHT_KEY) {
                    velX = getVelocityX();
                }
                if (e.getKeyCode() == KeybindingConfiguration.JUMP_KEY && !jumping) {
                    velY = -getVelocityY();
                    jumping = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeybindingConfiguration.LEFT_KEY || e.getKeyCode() == KeybindingConfiguration.RIGHT_KEY) {
                    velX = 0;
                }
            }
        };
    }

    public void actionPerformed() {
        position.setX(position.getX() + velX);
        position.setY(position.getY() + velY);

        if (position.getY() < state.floor) {
            velY += State.GRAVITY;
        } else {
            position.setY(state.floor);
            jumping = false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        BufferedImage playerImage;
        if (damaged) {
            playerImage = playerImageUnderDamage;
            damaged = false;
        } else {
            if (hp >= PLAYER_HP * 0.9) {
                playerImage = playerImageHealthHigh;
            } else if (hp >= PLAYER_HP * 0.5) {
                playerImage = playerImageHealthMedium;
            } else {
                playerImage = playerImageHealthLow;
            }
        }
        g.drawImage(playerImage, position.x, position.y - 40, null);
    }

    @Override
    public void reset() {
        velX = 0;
        velY = 0;
        jumping = false;
        hp = PLAYER_HP;
        position = new Position(50, 350);
    }

    private BufferedImage createPlayerImage(boolean underDamage, int currentHp) {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (underDamage) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.BLUE);
        }
        g2.fillOval(10, 10, 20, 25); // Body
        if (underDamage) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.BLACK);
        }
        g2.fillOval(15, 0, 10, 10); // Head
        if (currentHp >= PLAYER_HP * 0.9) {
            g2.setColor(Color.GREEN);
        } else if (currentHp >= PLAYER_HP * 0.5) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.RED);
        }
        g2.fillRect(10, 35, 8, 5); // Left leg
        g2.fillRect(22, 35, 8, 5); // Right leg
        g2.dispose();
        return image;
    }

    @Override
    public int hp() {
        return hp;
    }

    @Override
    public void damage(int damage) {
        velX = 0;
        hp -= damage;
        if (hp <= 0) {
            state.setGameOver(true);
        }
        damaged = true;
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
        return VELOCITY_Y;
    }
}
