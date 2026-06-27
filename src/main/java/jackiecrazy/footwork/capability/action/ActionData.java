package jackiecrazy.footwork.capability.action;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

public class ActionData implements ICapabilitySerializable<CompoundTag> {
    public static Capability<IAttachAction> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static IAttachAction OHNO = new DummyAttachActionCap();
    protected final IAttachAction instance;

    public ActionData() {
        this(new DummyAttachActionCap());
    }

    public ActionData(IAttachAction cap) {
        instance = cap;
    }

    private static final Map<Entity, IAttachAction> CACHE = new IdentityHashMap<>();
    public static IAttachAction getCap(Entity entity) {
//        if (entity == null || !entity.isAlive()) {
//            CACHE.remove(entity);
//            return OHNO;
//        }
//
//        return CACHE.computeIfAbsent(entity, e ->
            return    entity.getCapability(CAP).orElse(OHNO);
//        );
    }

    public static void flushCache() {
        CACHE.keySet().removeIf(e -> !e.isAlive());
    }
    public static void flushCache(Entity e) {
        CACHE.remove(e);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAP.orEmpty(cap, LazyOptional.of(() -> instance));
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
