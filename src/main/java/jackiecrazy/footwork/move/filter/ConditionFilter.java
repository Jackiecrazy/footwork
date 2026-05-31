package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.condition.Condition;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class ConditionFilter<T> extends Filter<T> {
    private Condition condition;
    @Override
    public List<T> filter(ArgumentContext argumentContext, List<T> targets) {
        return targets.stream().filter(a-> condition.resolve(new ArgumentContext(argumentContext.performer(), a instanceof Entity e ? e : null).copyContextFrom(argumentContext))).toList();
    }
}
