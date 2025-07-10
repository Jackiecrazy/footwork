package jackiecrazy.footwork.capability.timeslow;

import jackiecrazy.footwork.capability.stylish.IStyleCapability;
import jackiecrazy.footwork.capability.stylish.NoStyleCap;
import jackiecrazy.footwork.capability.timeslow.ITimeChange;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TimeSlowData implements ICapabilitySerializable<Tag> {
    private static ITimeChange OHNO = new TimeCapability();

    public static Capability<ITimeChange> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static ITimeChange getCap(Entity le) {
        return le.getCapability(CAP).orElse(OHNO);//.orElseThrow(() -> new IllegalArgumentException("attempted to find a nonexistent capability"));
    }


    private ITimeChange instance = new TimeCapability();

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
