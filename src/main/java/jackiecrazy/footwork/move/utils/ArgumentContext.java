package jackiecrazy.footwork.move.utils;

import net.minecraft.world.entity.Entity;

public class ArgumentContext {
    protected final Entity performer;
    protected final Entity target;

    public ArgumentContext(Entity performer, Entity target) {
        this.performer = performer;
        this.target = target;
    }

    public Entity performer() {
        return performer;
    }

    public Entity target() {
        return target;
    }
}
