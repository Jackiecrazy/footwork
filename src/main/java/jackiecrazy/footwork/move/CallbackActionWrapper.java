package jackiecrazy.footwork.move;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.motionframe.HitEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.Locale;

/**
 * a cut down version of ActionSetWrapper that only holds the context and a list of actions until it is triggered, at which point it will spawn an actually executable copy into your marks
 */
public class CallbackActionWrapper extends ActionSetWrapper {
    protected String key;
    protected boolean multifire = false;
    protected int duration;
    public CallbackActionWrapper(String triggerOn, int duration, List<Action> actions) {
        super(actions);
        this.duration = duration;
        key = triggerOn;
    }

    public void setMultiFire(boolean multifire) {
        this.multifire = multifire;
    }

    @Override
    public boolean executing() {
        return duration > 0;
    }

    @Override
    public void start(Entity performer, Entity target) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void tick(Entity performer, Entity target) {
        duration--;
    }

    @Override
    public void jumpTo(int jumpCode, Entity performer, Entity target) {
    }

    @Override
    public int trigger(Action action, Action parent, Entity performer, Entity target) {
        return 0;
    }

    public boolean triggerCallback(Entity performer, Entity target, String s) {
        if (s.equals(key)) {
            ActionData.getCap(target).mark(performer, new ActionSetWrapper(this.actions).withContext(context));
            if (!multifire)
                duration = -100;
            return true;
        }
        return false;
    }

    public static class HitEffectCAW extends CallbackActionWrapper {
        private HitEffects he;

        public HitEffectCAW(String triggerOn, int duration, HitEffects hit) {
            super(triggerOn, duration, List.of());
            he = hit;
        }

        @Override
        public boolean triggerCallback(Entity performer, Entity target, String s) {
            if (s.equals(key)) {
                if(he!=null && performer instanceof LivingEntity le)
                    he.runEffects(le, target, (InteractionHand) context.get("hand"), (ItemStack) context.get("itemstack"));
                if (!multifire)
                    duration = -100;
                return true;
            }
            return false;
        }
    }
}
