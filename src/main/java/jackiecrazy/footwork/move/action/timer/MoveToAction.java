package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MoveToAction extends TimerAction {
    private List<Action> tick = new ArrayList<>();
    private Argument<Double> speed_modifier = FixedNumberArgument.ONE;
    private Argument<Vec3> position;

    @Override
    public boolean canRun(ActionContext actionContext) {
        Vec3 moveTo = position.resolve(actionContext);
        if (actionContext.performer() instanceof Mob m) {
            Path path = m.getNavigation().createPath(moveTo.x, moveTo.y, moveTo.z, 0);
            if (path != null) {
                actionContext.wrapper().setData(this, path);
                m.getNavigation().moveTo(path, speed_modifier.resolve(actionContext));
                return super.canRun(actionContext);
                //fixme doesn't execute as part of base function
            }
        }
        return false;
    }

    @Override
    public void start(ActionSetWrapper wrapper, Entity performer, Entity target) {

        //m.getMoveControl().setWantedPosition(dir.x, dir.y, dir.z, speed_modifier.resolve(wrapper, this, performer, target));
        super.start(wrapper, performer, target);
    }

    @Override
    public boolean isFinished(ActionSetWrapper wrapper, Entity performer, Entity target) {
        if (super.isFinished(wrapper, performer, target)) return true;
        Path p = wrapper.getData(this);
        return p != null && p.isDone();
    }

    @Override
    public int tick(ActionSetWrapper wrapper, Entity performer, Entity target) {
        if(performer instanceof Mob m){
            m.getNavigation().tick();
        }
        int childRet = runActions(new ActionContext(wrapper, this, performer, target).copyContextFrom(wrapper), tick);
        if (childRet != 0) return childRet;
        return super.tick(wrapper, performer, target);
    }

    public void stop(ActionContext actionContext, boolean recursive) {
        if (actionContext.performer() instanceof Mob m)
            m.getNavigation().stop();
        if (recursive) {
            on_start.forEach(a -> a.stop(actionContext, true));
            tick.forEach(a -> a.stop(actionContext, true));
        }
        super.stop(actionContext, recursive);
    }

}
