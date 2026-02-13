package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

public class NotCondition extends Condition {
    Condition of;

    @Override
    public Boolean resolve(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target) {
        return !of.resolve(wrapper, parent, performer, target);
    }
}
