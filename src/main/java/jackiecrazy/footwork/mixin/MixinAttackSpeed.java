package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.event.DamageKnockbackEvent;
import jackiecrazy.footwork.utils.GeneralUtils;
import jackiecrazy.footwork.utils.MovementUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow
    public abstract void knockback(double p_147241_, double p_147242_, double p_147243_);

    @Inject(method = "hurt",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void mark(DamageSource ds, float amnt, CallbackInfoReturnable<Boolean> cir) {
        tempDS = ds;
    }

    @Redirect(method = "hurt", require = 0,
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void change(LivingEntity livingEntity, double strength, double ratioX, double ratioZ) {
        if (tempDS instanceof CombatDamageSource cds) {
            strength *= cds.getKnockbackPercentage();
            if (cds.getKnockbackVector() != null) {
                DamageKnockbackEvent mke = new DamageKnockbackEvent(livingEntity, tempDS, strength, cds.getKnockbackVector());
                MinecraftForge.EVENT_BUS.post(mke);
                strength = mke.getStrength();
                net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(livingEntity, (float) strength, ratioX, ratioZ);
                if (mke.isCanceled() || event.isCanceled()) {
                    tempDS = null;
                    return;
                }
                strength = event.getStrength();
                ratioX = event.getRatioX();
                ratioZ = event.getRatioZ();
                strength *= 1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                if (strength != 0) {
                    livingEntity.hasImpulse = true;
                    Vec3 currentMovement = livingEntity.getDeltaMovement();
                    Vec3 originalVector = new Vec3(ratioX, 0, ratioZ);
                    Vec3 addedMovement = MovementUtils.resolveVelocity(originalVector, mke.getDirection()).normalize().scale(strength);
                    livingEntity.setDeltaMovement(currentMovement.x / 2.0D - addedMovement.x, currentMovement.y / 2.0D - addedMovement.y, currentMovement.z / 2.0D - addedMovement.z);
                }
                tempDS = null;
                return;
            }
        }
        if (strength < 0 && tempDS.getEntity()!=null) {
            Entity from= tempDS.getEntity();
            //hooking type hits. not handled by LivingEntity, so we have to do it ourselves
            Vec3 distVec = livingEntity.position().add(0, livingEntity.getBbHeight() / 2, 0).vectorTo(from.position().add(0, from.getBbHeight() / 2, 0)).multiply(1, 0.5, 1).normalize();
            MovementUtils.knockBack(livingEntity, (float) strength, distVec.x, distVec.y, distVec.z, true);
        } else {
            livingEntity.knockback(strength, ratioX, ratioZ);
        }
        tempDS = null;
    }
}