package jackiecrazy.footwork.capability.timeslow;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jackiecrazy.footwork.capability.action.IAttachAction;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TimeSlowData implements ICapabilitySerializable<Tag> {
    public TimeSlowData(Entity bound) {
        this.instance = new TimeCapability(bound);
    }

    public TimeSlowData() {
    }

    private static ITimeChange OHNO = new TimeCapability();

    public static Capability<ITimeChange> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    private static final Map<Entity, ITimeChange> CACHE = new IdentityHashMap<>();
    public static ITimeChange getCap(Entity entity) {
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
