package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.condition.Condition;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class ConditionFilter<T> extends Filter<T> {
    private Condition condition;
    @Override
    public List<T> filter(ActionSetWrapper wrapper, Action parent, Entity performer, Entity target, List<T> targets) {
        return targets.stream().filter(a->condition.resolve(wrapper, parent, performer, a instanceof Entity e?e:null)).toList();
    }
}
