package co.axelrod.voidwalker;

public class GameApplication {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        Game game = new Game();
        Thread frameVTSThread = new Thread(new FrameVTSThread(game));
        Thread updateFTSThread = new Thread(new UpdateFTSThread(game));
        frameVTSThread.start();
        updateFTSThread.start();
    }
}
