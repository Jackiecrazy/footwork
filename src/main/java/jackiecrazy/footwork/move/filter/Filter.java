package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.Move;
import net.minecraft.world.entity.Entity;

import java.util.List;

public abstract class Filter<T> extends Move {
    protected String ID;
    protected int limit;

    public abstract List<T> filter(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target, List<T> targets);

    public String toString() {
        return ID;
    }
}
