package co.axelrod.voidwalker.sprite;

import co.axelrod.voidwalker.model.Drawable;
import co.axelrod.voidwalker.model.HitBox;
import co.axelrod.voidwalker.model.Position;
import co.axelrod.voidwalker.model.feature.*;
import co.axelrod.voidwalker.model.game.State;

import java.awt.*;
import java.awt.image.BufferedImage;

import static co.axelrod.voidwalker.config.HealthConfiguration.HEART_HP;

public class Heart extends Drawable implements Interactable, Destroyable, Collidable, Movable {
    private static final int VELOCITY_X = 1;

    private final Player player;

    private boolean hit = false;
    private int hp = HEART_HP;

    private final State state;

    private BufferedImage image = createImage();

    public Heart(String name, Player player, State state) {
        super(name, new Position(state.width, state.floor - player.hitBox.getHeight() - 40), new HitBox(40, 40));
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

        hp = HEART_HP;
        position.setX(state.width);
        position.setY(state.floor - player.hitBox.getHeight() + 10);
        hitBox.setWidth(30);
        hitBox.setHeight(30);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (hit) {
            return;
        }

        g.drawImage(image, position.x, position.y, null);
    }

    private BufferedImage createImage() {
        BufferedImage image = new BufferedImage(hitBox.getWidth(), hitBox.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Устанавливаем красный цвет
        g2.setColor(Color.RED);

        // Устанавливаем сглаживание
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем сердце с использованием полигонов и дуг
        int x = 10, y = 10, size = 30; // Начальные координаты и размер

        // Рисуем две дуги (верхняя часть сердца)
        g2.fillArc(x, y, size / 2, size / 2, 0, 180);
        g2.fillArc(x + size / 2, y, size / 2, size / 2, 0, 180);

        // Рисуем треугольник (нижняя часть сердца)
        int[] xPoints = {x, x + size, x + size / 2};
        int[] yPoints = {y + size / 4, y + size / 4, y + size};
        g2.fillPolygon(xPoints, yPoints, 3);

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
        hit = true;
    }

    @Override
    public boolean readyToDestroy() {
        return hit || position.x < 0;
    }

    @Override
    public int getVelocityX() {
        return -VELOCITY_X * state.getVelocityFactor();
    }

    @Override
    public int getVelocityY() {
        return 0;
    }
}
