package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

public class FalseCondition extends Condition{
    public static final FalseCondition INSTANCE=new FalseCondition();
    @Override
    public Boolean resolve(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target) {
        return false;
    }
}
