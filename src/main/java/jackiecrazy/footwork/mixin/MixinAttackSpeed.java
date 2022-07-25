package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.api.WarAttributes;
import jackiecrazy.footwork.event.DamageKnockbackEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinAttackSpeed {

    DamageSource tempDS = null;
    DamageKnockbackEvent dke=null;

    @Inject(method = "hurt",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/entity/LivingEntity;knockback(FDD)V"))
    private void mark(DamageSource ds, float amnt, CallbackInfoReturnable<Boolean> cir) {
        tempDS = ds;
    }

    @Redirect(method = "hurt",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/LivingEntity;knockback(FDD)V"))
    private void change(LivingEntity livingEntity, float strength, double ratioX, double ratioZ) {
        DamageKnockbackEvent mke = new DamageKnockbackEvent(livingEntity, tempDS, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post(mke);
        livingEntity.knockback(mke.getStrength(), mke.getRatioX(), mke.getRatioZ());
        tempDS = null;
    }
}