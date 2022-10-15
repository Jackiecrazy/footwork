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
    public boolean m_8036_() {
        return (CombatData.getCap(e).isValid() && CombatData.getCap(e).getStaggerTime() > 0) || e.m_21023_(FootworkEffects.PETRIFY.get()) || e.m_21023_(FootworkEffects.PARALYSIS.get()) || e.m_21023_(FootworkEffects.SLEEP.get());
    }

    @Override
    public boolean m_6767_() {
        return false;
    }

    @Override
    public EnumSet<Flag> m_7684_() {
        return mutex;
    }
}
