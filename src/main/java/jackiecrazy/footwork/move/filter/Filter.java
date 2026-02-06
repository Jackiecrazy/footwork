package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.Move;
import net.minecraft.world.entity.Entity;

import java.util.List;

public abstract class Filter<T> extends Move {
    protected String ID;
    protected int limit;

    public abstract List<T> filter(ActionSetWrapper wrapper, Action parent, Entity performer, Entity target, List<T> targets);

    public String toString() {
        return ID;
    }
}
