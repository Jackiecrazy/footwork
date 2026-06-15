package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.action.trigger.Trigger;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;


public abstract class TimerAction extends Action {
    protected List<Action> on_start = new ArrayList<>();
    protected List<Action> on_stop = new ArrayList<>();

    public List<Trigger> getTriggers() {
        return triggers;
    }

    private List<Trigger> triggers=new ArrayList<>();
    /**
     * on the base action
     */
    private Argument<Double> max_time= FixedNumberArgument.ZERO;

    @Override
    public boolean canRun(ActionContext actionContext) {
        if (actionContext.wrapper().getTimer(this) >= 0 && !isFinished(actionContext.wrapper(), actionContext.performer(), actionContext.target()))//isn't done, but has already started
            return true;
        return super.canRun(actionContext);
    }

    /**
     * @return false if the action is still running
     */
    public boolean isFinished(ActionSetWrapper wrapper, Entity performer, Entity target) {
        return wrapper.getTimer(this) > max_time.resolve(wrapper.generateContext(performer, target, this)) || wrapper.getTimer(this) < 0;
    }

    public int tick(ActionSetWrapper wrapper, Entity performer, Entity target) {
        return isFinished(wrapper, performer, target) ? -1 : 0;
    }

    public void start(ActionSetWrapper wrapper, Entity performer, Entity target) {
        runActions(wrapper.generateContext(performer, target, this), on_start);
    }

    public int perform(ActionContext actionContext) {
        return tick(actionContext.wrapper(), actionContext.performer(), actionContext.target());
    }

    public void stop(ActionContext actionContext, boolean recursive) {
        actionContext.wrapper().immediatelyExpire(this);
        runActions(actionContext.wrapper().generateContext(actionContext.performer(), actionContext.target(), this), on_stop);
        if(recursive)
            on_start.forEach(a->a.stop(actionContext, true));
    }


}
