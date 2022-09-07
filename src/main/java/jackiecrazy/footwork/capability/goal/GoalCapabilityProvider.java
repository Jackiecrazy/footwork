package jackiecrazy.footwork.capability.goal;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GoalCapabilityProvider implements ICapabilitySerializable<Tag> {

    public static Capability<IGoalHelper> CAP = null;

    public static LazyOptional<IGoalHelper> getCap(LivingEntity le) {
        return le.getCapability(CAP);//.orElseThrow(() -> new IllegalArgumentException("attempted to find a nonexistent capability"));
    }


    private IGoalHelper instance = new GoalCapability();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAP.orEmpty(cap, LazyOptional.of(()->instance));
    }

    @Override
    public Tag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(Tag nbt) {

    }
}
