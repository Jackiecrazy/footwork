package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.event.DamageKnockbackEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinKnockbackHook {

    DamageSource tempDS = null;
    DamageKnockbackEvent dke=null;

    @Inject(method = "hurtServer",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void mark(ServerLevel p_376221_, DamageSource ds, float p_376610_, CallbackInfoReturnable<Boolean> cir) {
        tempDS = ds;
    }

    @Redirect(method = "hurtServer",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void change(LivingEntity livingEntity, double strength, double ratioX, double ratioZ) {
        DamageKnockbackEvent mke = new DamageKnockbackEvent(livingEntity, tempDS, strength, ratioX, ratioZ);
        NeoForge.EVENT_BUS.post(mke);
        livingEntity.knockback(mke.getStrength(), mke.getRatioX(), mke.getRatioZ());
        tempDS = null;
    }
}