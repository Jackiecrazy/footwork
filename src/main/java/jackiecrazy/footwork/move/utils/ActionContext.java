package jackiecrazy.footwork.move.utils;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public final class ActionContext extends ArgumentContext {
    private final ActionSetWrapper wrapper;
    private final Action parent;

    public ActionContext(ActionSetWrapper wrapper, Action parent, Entity performer, Entity target) {
        super(performer, target);
        this.wrapper = wrapper;
        this.parent = parent;
    }

    public ActionSetWrapper wrapper() {
        return wrapper;
    }

    public Action parent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ActionContext) obj;
        return Objects.equals(this.wrapper, that.wrapper) &&
                Objects.equals(this.parent, that.parent) &&
                Objects.equals(this.performer(), that.performer()) &&
                Objects.equals(this.target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrapper, parent, performer(), target);
    }

    @Override
    public String toString() {
        return "ActionContext[" +
                "wrapper=" + wrapper + ", " +
                "parent=" + parent + ", " +
                "performer=" + performer() + ", " +
                "target=" + target + ']';
    }

    public ActionContext copyContextFrom(ArgumentContext other){
        return (ActionContext) addContext(other.context);
    }

    public ActionContext copyContextFrom(ActionSetWrapper other){
        return (ActionContext) addContext(other.context);
    }

}