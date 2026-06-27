package jackiecrazy.footwork.capability.stylish;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jackiecrazy.footwork.capability.action.IAttachAction;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class StylishData implements ICapabilitySerializable<CompoundTag> {
    private static IStyleCapability OHNO = new NoStyleCap();

    public static Capability<IStyleCapability> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    private static final Map<Entity, IStyleCapability> CACHE = new IdentityHashMap<>();
    public static IStyleCapability getCap(LivingEntity entity) {
//        if (entity == null || !entity.isAlive()) {
//            CACHE.remove(entity);
//            return OHNO;
//        }
//
//        return CACHE.computeIfAbsent(entity, e ->
                return entity.getCapability(CAP).orElse(OHNO);
//        );
    }

    public static void flushCache() {
        CACHE.keySet().removeIf(e -> !e.isAlive());
    }
    public static void flushCache(Entity e) {
        CACHE.remove(e);
    }

    protected final IStyleCapability instance;

    public StylishData() {
        this(new NoStyleCap());
    }

    public StylishData(IStyleCapability cap) {
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
