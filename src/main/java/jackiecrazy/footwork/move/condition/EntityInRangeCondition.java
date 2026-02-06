package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.SelectorArgument;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class EntityInRangeCondition extends Condition{
    private SelectorArgument select;

    @Override
    public Boolean resolve(ActionSetWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        return !select.resolve(wrapper, parent, performer, target).isEmpty();
    }
}
