package jackiecrazy.footwork.entity.ai;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.goal.GoalCapabilityProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class CompelledVengeanceGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private int compelledTicks = 0;

    public CompelledVengeanceGoal(Mob p_i50313_1_) {
        super(p_i50313_1_, LivingEntity.class, false);
    }

    @Override
    protected void m_26073_() {
        f_26135_.getCapability(GoalCapabilityProvider.CAP).ifPresent(a -> {
            LivingEntity potential = a.getForcedTarget();
            if (potential != null && f_26135_.m_20280_(potential) < 100)
                f_26050_ = potential;
        });
    }

    @Override
    public void m_8037_() {
        super.m_8037_();
        compelledTicks--;
    }

    @Override
    public void m_8056_() {
        super.m_8056_();
        compelledTicks = Footwork.rand.nextInt(100)+40;
    }

    @Override
    public void m_8041_() {
        super.m_8041_();
        f_26050_=null;
        f_26135_.getCapability(GoalCapabilityProvider.CAP).ifPresent(a -> a.setForcedTarget(null));
    }

    @Override
    public boolean m_8036_() {
        return super.m_8036_();
    }

    @Override
    public boolean m_8045_() {
        if (compelledTicks < 0) return false;
        return super.m_8045_();
    }
}
