package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.vector.RawVectorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class AddVelocityAction extends TimerAction {
    private List<Action> on_launch=new ArrayList<>();
    private List<Action> tick=new ArrayList<>();
    private List<Action> on_land=new ArrayList<>();
    private Argument<Vec3> direction= RawVectorArgument.ZERO;

    @Override
    public void start(ActionSetWrapper wrapper, Entity performer, Entity target) {
        runActions(new ActionContext(wrapper, this, performer, target), on_launch);
        Vec3 dir = direction.resolve(new ActionContext(wrapper, this, performer, target));
        performer.addDeltaMovement(dir);
        wrapper.setData(this, false);
        if (dir.y > 0) {
            performer.setOnGround(false);
            wrapper.setData(this, true);
        }
        super.start(wrapper, performer, target);
    }

    @Override
    public int tick(ActionSetWrapper wrapper, Entity performer, Entity target) {
        int childRet = runActions(new ActionContext(wrapper, this, performer, target), tick);
        if (childRet != 0) return childRet;
        if (!performer.onGround()) {
            wrapper.setData(this, true);
        } else if (wrapper.getData(this)) {
            wrapper.setData(this, false);
            childRet = runActions(new ActionContext(wrapper, this, performer, target), on_land);
            if (childRet != 0) return childRet;
        }
        return super.tick(wrapper, performer, target);
    }

    public void stop(ActionContext actionContext, boolean recursive) {
        if(recursive){
            on_launch.forEach(a->a.stop(actionContext, true));
            tick.forEach(a->a.stop(actionContext, true));
            on_land.forEach(a->a.stop(actionContext, true));
        }
        super.stop(actionContext, recursive);
    }

}
