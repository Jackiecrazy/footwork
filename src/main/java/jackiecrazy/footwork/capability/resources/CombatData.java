package jackiecrazy.footwork.capability.resources;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CombatData implements ICapabilitySerializable<CompoundTag> {
    private static ICombatCapability OHNO = new DummyCombatCap();

    public static Capability<ICombatCapability> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static ICombatCapability getCap(LivingEntity le) {
        return le.getCapability(CAP).orElse(OHNO);//.orElseThrow(() -> new IllegalArgumentException("attempted to find a nonexistent capability"));
    }

    protected final ICombatCapability instance;

    public CombatData() {
        this(new DummyCombatCap());
    }

    public CombatData(ICombatCapability cap) {
        instance = cap;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAP.orEmpty(cap, LazyOptional.of(() -> instance));
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.write();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.read(nbt);
    }
}
