package jackiecrazy.footwork.entity.ai;

import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class NoGoal extends Goal {
    static final EnumSet<Flag> mutex = EnumSet.allOf(Flag.class);
    LivingEntity e;

    public NoGoal(LivingEntity bind) {
        e = bind;
    }

    @Override
    public boolean canUse() {
        return (CombatData.getCap(e).isValid() && CombatData.getCap(e).getStaggerTime() > 0) || e.hasEffect(FootworkEffects.PETRIFY.get()) || e.hasEffect(FootworkEffects.PARALYSIS.get()) || e.hasEffect(FootworkEffects.SLEEP.get());
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public EnumSet<Flag> getFlags() {
        return mutex;
    }
}
