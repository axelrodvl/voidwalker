package co.axelrod.voidwalker.sprite;

import co.axelrod.voidwalker.model.Drawable;
import co.axelrod.voidwalker.model.HitBox;
import co.axelrod.voidwalker.model.Position;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FractalTree extends Drawable {
    private final int initialLength;

    private static final double ANGLE = Math.PI / 6; // 30 degrees
    private static final double SCALE = 0.7; // Scale factor for branch length

    private final BufferedImage bufferedImage;

    public FractalTree(String name, Position position, HitBox hitBox, int initialLength) {
        super(name, position, hitBox);
        this.initialLength = initialLength;
        bufferedImage = createImage();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(bufferedImage, position.x - initialLength * 2, position.y - initialLength * 3 - 50, null);
    }

    @Override
    public void reset() {

    }

    private void drawTree(Graphics g, int x1, int y1, double angle, double length) {
        if (length < 1) return; // Stop when branches become too small

        int x2 = x1 + (int) (length * Math.cos(angle));
        int y2 = y1 + (int) (length * Math.sin(angle));

        if (length > 10) {
            g.setColor(new Color(110, 67, 24));
        } else {
            g.setColor(new Color(0x6FA545));
        }
        g.drawLine(x1, y1, x2, y2);

        drawTree(g, x2, y2, angle - ANGLE, length * SCALE);
        drawTree(g, x2, y2, angle + ANGLE, length * SCALE);
    }

    private BufferedImage createImage() {
        BufferedImage image = new BufferedImage(initialLength * 4, initialLength * 3, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTree(g2, initialLength * 4 / 2, initialLength * 3, -Math.PI / 2, initialLength);
        g2.dispose();
        return image;
    }
}
