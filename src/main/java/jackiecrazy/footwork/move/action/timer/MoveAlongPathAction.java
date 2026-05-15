package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.motionframe.FrameEffects;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManager;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MoveAlongPathAction extends TimerAction {
    private List<Action> tick = new ArrayList<>();
    private Argument<Entity> subject=ArgumentContext::target;
    private MotionManager path;
    private Argument<Vec3> position;
    private Argument<Vec3> offset;

    @Override
    public void start(ActionSetWrapper wrapper, Entity performer, Entity target) {
        super.start(wrapper, performer, target);
    }

    @Override
    public boolean isFinished(ActionSetWrapper wrapper, Entity performer, Entity target) {
        if (super.isFinished(wrapper, performer, target)) return true;
        return wrapper.getTimer(this)>path.getDuration();
    }

    @Override
    public int tick(ActionSetWrapper wrapper, Entity performer, Entity target) {
        MotionManager activeMove = path;
        if (activeMove == null) return 0;
        MotionFrame update = activeMove.getNextPoint(wrapper.getTimer(this));
//        if (update.effects() != currentEffects) {
//            updateFrameEffects(update.effects());
//        }
        final ActionContext ctx = new ActionContext(wrapper, this, performer, target);
        final Tuple<Vec3, Vec3> bundle = new Tuple<>(position.resolve(ctx), offset.resolve(ctx));
        Vec3 transformedDirection = update.resolveTargetOffset(bundle, Vec3.ZERO, 0);

        target.setPos(transformedDirection);
        int childRet = runActions(ctx, tick);
        if (childRet != 0) return childRet;
        return super.tick(wrapper, performer, target);
    }

    protected void updateFrameEffects(FrameEffects effects) {
//        currentEffects = effects;
//        if (effects != null) {
//            setIntangible(false);
//            if (effects.getRange() >= 0) setInteractionRange((float) effects.getRange());
//            if (effects.getEffects() != null)
//                setEffect(effects.getEffects().toArray(new FlyingWeaponEffect[0]));
//            cacheInfo = effects.getHit();
//            if (cacheInfo != null && getOwner() != null)
//                CombatUtils.applyFrames(getOwner(), cacheInfo);
//            if (effects.reset_hit())
//                alreadyHit.clear();
//            if (effects.shouldUndrag())
//                unDrag();
//            LivingEntity e = getOwner();
//            if (e != null)
//                effects.runEffects(e, e);
//            if(effects.getDisplayItems()!=null)
//                setCosmeticItem(effects.getDisplayItems().resolve(new ArgumentContext(getOwner(), getOwner())));
//        }
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
