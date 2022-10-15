package jackiecrazy.footwork.capability.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

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
