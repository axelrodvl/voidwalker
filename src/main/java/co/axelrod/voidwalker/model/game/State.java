package co.axelrod.voidwalker.model.game;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.ResourceBundle;

@Data
@Getter
@Setter
public class State {
    public static final int TARGET_FTS = 60;

    public static final int TARGET_FPS = 120;
    public static final boolean FRAME_LIMIT = false;

    public int width = 800; // 800
    public int height = 400; // 400

    public int floor = height - 50;

    public static final int GRAVITY = 1;

    private boolean gameOver = false;

    private int velocityFactor = 1;

    private int score = 0;

    public static final Locale LOCALE = Locale.US;
    public static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", LOCALE);
}
