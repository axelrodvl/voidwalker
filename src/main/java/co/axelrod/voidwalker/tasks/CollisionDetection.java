package co.axelrod.voidwalker.tasks;

import co.axelrod.voidwalker.model.feature.Destroyable;
import co.axelrod.voidwalker.model.Drawable;
import co.axelrod.voidwalker.model.feature.Collidable;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CollisionDetection {
    private final List<Drawable> drawables;

    public CollisionDetection(List<Drawable> drawables) {
        this.drawables = drawables;
    }

    public void detectCollisions() {
        List<Drawable> collidables = drawables.stream()
                .filter(Collidable.class::isInstance)
                .toList();

        for (int i = 0; i < collidables.size(); i++) {
            for (int j = 0; j < collidables.size(); j++) {
                if (i == j) {
                    continue;
                }

                Drawable drawable1 = collidables.get(i);
                Drawable drawable2 = collidables.get(j);

                if (drawable1.position.x >= drawable2.position.x
                        && drawable1.position.x + drawable1.hitBox.getWidth() < drawable2.position.x + drawable2.hitBox.getWidth()
                        && drawable1.position.y > drawable2.position.y) {
                    if (drawable1 instanceof Destroyable && drawable2 instanceof Destroyable) {
                        Destroyable destroyable1 = (Destroyable) drawable1;
                        Destroyable destroyable2 = (Destroyable) drawable2;
                        int damage = Math.min(destroyable1.hp(), destroyable2.hp());
                        destroyable1.damage(damage);
                        destroyable2.damage(damage);
                    }

                    log.info("Collision detected: between {} and {}", drawable1, drawable2);
                }
            }
        }
    }
}
