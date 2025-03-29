package co.axelrod.voidwalker.model.feature;

public interface Destroyable {
    int hp();
    void damage(int damage);
    boolean readyToDestroy();
}
