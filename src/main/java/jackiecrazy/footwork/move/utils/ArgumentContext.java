package jackiecrazy.footwork.move.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ArgumentContext {
    protected final Entity performer;
    protected final Entity target;
    protected Vec3 position;

    public ArgumentContext(Entity performer, Entity target) {
        this.performer = performer;
        this.target = target;
        position=performer.position();
    }

    public Entity performer() {
        return performer;
    }

    public Entity target() {
        return target;
    }

    public ArgumentContext setPosition(Vec3 pos){
        position = pos;
        return this;
    }

    public Vec3 getContextualPosition() {
        return position;
    }
}
