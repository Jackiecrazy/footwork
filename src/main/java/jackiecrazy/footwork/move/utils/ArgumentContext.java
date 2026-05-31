package jackiecrazy.footwork.move.utils;

import jackiecrazy.footwork.move.ActionSetWrapper;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;

public class ArgumentContext {
    protected final Entity performer;
    protected final Entity target;
    public final HashMap<String, Object> context = new HashMap<>();

    public ArgumentContext(Entity performer, Entity target) {
        this.performer = performer;
        this.target = target;
        addContext("position", target.position());//using target position makes the most sense here for targeting effects
    }

    public Entity performer() {
        return performer;
    }

    public Entity target() {
        return target;
    }

    public <T> T getContext(String a) {
        return (T) (context.get(a));
    }

    public ArgumentContext addContext(HashMap<String, Object> ctx){
        context.putAll(ctx);
        return this;
    }

    public ArgumentContext addContext(String a, Object b) {
        context.put(a, b);
        return this;
    }

    public ArgumentContext copyContextFrom(ArgumentContext other){
        return addContext(other.context);
    }

    public ArgumentContext copyContextFrom(ActionSetWrapper other){
        return addContext(other.context);
    }

}
