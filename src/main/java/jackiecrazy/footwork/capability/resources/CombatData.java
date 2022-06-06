package jackiecrazy.footwork.capability.resources;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CombatData implements ICapabilitySerializable<CompoundTag> {
    private static ICombatCapability OHNO=new DummyCombatCap();

    @CapabilityInject(ICombatCapability.class)
    public static Capability<ICombatCapability> CAP = null;

    public static ICombatCapability getCap(LivingEntity le) {
        return le.getCapability(CAP).orElse(OHNO);//.orElseThrow(() -> new IllegalArgumentException("attempted to find a nonexistent capability"));
    }

    private final LazyOptional<ICombatCapability> instance;

    public CombatData() {
        instance = LazyOptional.of(() -> CAP.getDefaultInstance());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAP.orEmpty(cap, instance);
    }

    @Override
    public CompoundTag serializeNBT() {
        return (CompoundTag) CAP.getStorage().writeNBT(
                CAP,
                instance.orElseThrow(() ->
                        new IllegalArgumentException("LazyOptional cannot be empty!")),
                null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        CAP.getStorage().readNBT(
                CAP,
                instance.orElseThrow(() ->
                        new IllegalArgumentException("LazyOptional cannot be empty!")),
                null, nbt);
    }
}
