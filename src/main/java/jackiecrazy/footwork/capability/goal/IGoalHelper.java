package jackiecrazy.footwork.capability.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public interface IGoalHelper {
    void setForcedTarget(LivingEntity e);
    LivingEntity getForcedTarget();
    void setFearSource(LivingEntity e);
    LivingEntity getFearSource();
    void setSoundLocation(BlockPos pos);
    BlockPos getSoundLocation();
}
