package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

public class TrueCondition extends Condition {
    public static final TrueCondition INSTANCE = new TrueCondition();

    @Override
    public Boolean resolve(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target) {
        return true;
    }
}
