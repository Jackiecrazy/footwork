package jackiecrazy.footwork.capability.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public class GoalCapability implements IGoalHelper {
    private BlockPos p = BlockPos.ZERO.below(100);
    private LivingEntity target;
    private LivingEntity fear;

    public GoalCapability() {}

    @Override
    public LivingEntity getForcedTarget() {
        return target;
    }

    @Override
    public void setForcedTarget(LivingEntity e) {
        target = e;
    }

    @Override
    public LivingEntity getFearSource() {
        if (fear != null && fear.isDeadOrDying()) fear = null;
        return fear;
    }

    @Override
    public void setFearSource(LivingEntity e) {
        fear = e;
    }

    @Override
    public BlockPos getSoundLocation() {
        return p;
    }

    @Override
    public void setSoundLocation(BlockPos pos) {
        p = pos;
    }
}
