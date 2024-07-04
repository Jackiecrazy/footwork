package jackiecrazy.footwork.entity.ai;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.goal.GoalCapabilityProvider;
import jackiecrazy.footwork.entity.FootworkDataAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class CompelledVengeanceGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private int compelledTicks = 0;

    public CompelledVengeanceGoal(Mob p_i50313_1_) {
        super(p_i50313_1_, LivingEntity.class, false);
    }

    @Override
    protected void findTarget() {
        LivingEntity potential = mob.getData(FootworkDataAttachments.FEAR_TARGET);
        if (potential != null && mob.distanceToSqr(potential) < 100)
            target = potential;
    }

    @Override
    public void tick() {
        super.tick();
        compelledTicks--;
    }

    @Override
    public void start() {
        super.start();
        compelledTicks = Footwork.rand.nextInt(100) + 40;
    }

    @Override
    public void stop() {
        super.stop();
        target = null;
        mob.setData(FootworkDataAttachments.FEAR_TARGET, null);
    }

    @Override
    public boolean canUse() {
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (compelledTicks < 0) return false;
        return super.canContinueToUse();
    }
}
