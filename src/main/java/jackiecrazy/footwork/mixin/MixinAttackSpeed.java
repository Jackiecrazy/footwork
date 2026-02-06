package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.event.DamageKnockbackEvent;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinAttackSpeed extends Entity {

    DamageSource tempDS = null;
    DamageKnockbackEvent dke = null;

    public MixinAttackSpeed(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "hurt",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void mark(DamageSource ds, float amnt, CallbackInfoReturnable<Boolean> cir) {
        tempDS = ds;
    }

    @Redirect(method = "hurt", require = 0,
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void change(LivingEntity livingEntity, double strength, double ratioX, double ratioZ) {
        DamageKnockbackEvent mke = new DamageKnockbackEvent(livingEntity, tempDS, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post(mke);
        livingEntity.knockback(mke.getStrength(), mke.getRatioX(), mke.getRatioZ());
        tempDS = null;
    }
}