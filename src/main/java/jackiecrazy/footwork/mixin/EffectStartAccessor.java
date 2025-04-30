package jackiecrazy.footwork.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public interface EffectStartAccessor {
    @Invoker
    void callOnEffectAdded(MobEffectInstance pEffectInstance, @Nullable Entity pEntity);
}
