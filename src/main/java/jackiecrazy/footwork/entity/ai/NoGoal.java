package jackiecrazy.footwork.entity.ai;

import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class NoGoal extends Goal {
    static final EnumSet<Flag> mutex = EnumSet.allOf(Flag.class);
    LivingEntity e;

    public NoGoal(LivingEntity bind) {
        e = bind;
    }

    @Override
    public boolean canUse() {
        return e.hasEffect(FootworkEffects.PETRIFY) || e.hasEffect(FootworkEffects.PARALYSIS) || e.hasEffect(FootworkEffects.SLEEP);
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
