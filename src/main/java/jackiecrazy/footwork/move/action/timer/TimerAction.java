package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.action.trigger.Trigger;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public abstract class TimerAction extends Action {
    public List<Trigger> getTriggers() {
        return triggers;
    }

    private List<Trigger> triggers=new ArrayList<>();
    /**
     * on the base action
     */
    private Argument<Double> max_time= FixedNumberArgument.ZERO;

    @Override
    public boolean canRun(ActionSetWrapper wrapper, Action parent, Entity performer, Entity target) {
        if (wrapper.getTimer(this) >= 0 && !isFinished(wrapper, performer, target))//isn't done, but has already started
            return true;
        return super.canRun(wrapper, parent, performer, target);
    }

    /**
     * @return false if the action is still running
     */
    public boolean isFinished(ActionSetWrapper wrapper, Entity performer, Entity target) {
        return wrapper.getTimer(this) > max_time.resolve(wrapper, this, performer, target) || wrapper.getTimer(this) < 0;
    }

    public int tick(ActionSetWrapper wrapper, Entity performer, Entity target) {
        return isFinished(wrapper, performer, target) ? -1 : 0;
    }

    public void start(ActionSetWrapper wrapper, Entity performer, Entity target) {
    }

    public int perform(ActionSetWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        return tick(wrapper, performer, target);
    }

    public void stop(ActionSetWrapper wrapper, Entity performer, Entity target, boolean recursive) {
        wrapper.immediatelyExpire(this);
    }


}
