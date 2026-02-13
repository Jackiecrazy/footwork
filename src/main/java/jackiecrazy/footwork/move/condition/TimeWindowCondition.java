package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import net.minecraft.world.entity.Entity;

public class TimeWindowCondition extends Condition {

    private int from = 0, to = Integer.MAX_VALUE, every = 1;

    @Override
    public Boolean resolve(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target) {
        if (!(parent instanceof TimerAction ta)) return false;
        int time = wrapper.getTimer(ta);
        return time >= from && time < to && (time - from) % every == 0;
    }
}
