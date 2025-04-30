package jackiecrazy.footwork.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public class MixinKnockbackHook {

//    DamageSource tempDS = null;
//    DamageKnockbackEvent dke=null;
//
//    @Inject(method = "hurtServer",
//            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
//    private void mark(ServerLevel p_376221_, DamageSource ds, float p_376610_, CallbackInfoReturnable<Boolean> cir) {
//        tempDS = ds;
//    }
//
//    @Redirect(method = "hurtServer",
//            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
//    private void change(LivingEntity livingEntity, double strength, double ratioX, double ratioZ) {
//        DamageKnockbackEvent mke = new DamageKnockbackEvent(livingEntity, tempDS, strength, ratioX, ratioZ);
//        NeoForge.EVENT_BUS.post(mke);
//        livingEntity.knockback(mke.getStrength(), mke.getRatioX(), mke.getRatioZ());
//        tempDS = null;
//    }
}