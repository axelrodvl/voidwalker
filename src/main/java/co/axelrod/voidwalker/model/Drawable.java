package co.axelrod.voidwalker.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.awt.*;

@AllArgsConstructor
@ToString
public abstract class Drawable {
    public String name;
    public Position position;
    public HitBox hitBox;

    public abstract void paintComponent(Graphics g);

    public abstract void reset();
}
