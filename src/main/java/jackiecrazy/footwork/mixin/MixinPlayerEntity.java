package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.api.FootworkDamageArchetype;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.event.MeleeKnockbackEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Shadow
    public abstract void remove(RemovalReason p_150097_);

    private static boolean tempCrit;
    private static float tempCdmg;
    private static DamageSource ds;


//    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
//            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/player/PlayerEntity;resetCooldown()V"))
//    private void noReset(Entity targetEntity, CallbackInfo ci, float f, float f1, float f2) {
//        CombatData.getCap(this).setCachedCooldown(f2);
//    } //Mohist why


    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
    private void store(Entity target, CallbackInfo ci, float f, ItemStack itemstack, DamageSource damagesource, float f1, float f2, boolean flag3, boolean flag, boolean flag1, CriticalHitEvent critEvent, float f3, boolean flag2, boolean critBlocksSweep, SweepAttackEvent sweepEvent, float f6, LivingEntity var16) {
        ((CombatDamageSource)damagesource).setDamageDealer(getMainHandItem()).setAttackingHand(CombatData.getCap(this).isOffhandAttack() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).setProcAttackEffects(true).setProcNormalEffects(true).setCrit(tempCrit).setCritDamage(tempCdmg).setDamageTyping(FootworkDamageArchetype.PHYSICAL);
        tempCrit = flag2;
        tempCdmg = critEvent == null ? 1 : critEvent.getDamageMultiplier();
    }

//    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
//            at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
//    private void customDamageSource(Entity target, CallbackInfo ci) {
//        CombatDamageSourceMixin d = new CombatDamageSourceMixin(player);
//        ds = d;
//        return d;
//    }

    @Inject(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void stats(Entity targetEntity, CallbackInfo ci) {
        targetEntity.invulnerableTime = 0;
        if (targetEntity instanceof LivingEntity) {
            ((LivingEntity) targetEntity).hurtTime = ((LivingEntity) targetEntity).hurtDuration = 0;
        }
    }

    /*@ModifyVariable(method = "actuallyHurt",
            at = @At(value = "STORE"), name = "p_36313_")
    private float absorption(float amount) {
        //makes absorption block true damage
        return amount;
    }*/

    @Redirect(method = "attack",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void mark(LivingEntity livingEntity, double strength, double ratioX, double ratioZ) {
        //fixme this only happens if the item has knockback enchant
        MeleeKnockbackEvent mke = new MeleeKnockbackEvent(this, ds, livingEntity, strength, ratioX, ratioZ);
        NeoForge.EVENT_BUS.post(mke);
        livingEntity.knockback(mke.getStrength(), mke.getRatioX(), mke.getRatioZ());
    }
}
