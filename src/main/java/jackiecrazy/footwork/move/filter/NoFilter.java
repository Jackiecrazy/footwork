package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class NoFilter<T> extends Filter<T> {
    public static final NoFilter INSTANCE=new NoFilter();
    @Override
    public List<T> filter(ActionSetWrapper wrapper, Action parent, Entity performer, Entity target, List<T> targets) {
        return targets;
    }
}
