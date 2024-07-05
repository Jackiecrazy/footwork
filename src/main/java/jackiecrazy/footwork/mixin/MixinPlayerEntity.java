package jackiecrazy.footwork.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.event.MeleeKnockbackEvent;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    private static DamageSource ds;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

//    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
//            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/player/PlayerEntity;resetCooldown()V"))
//    private void noReset(Entity targetEntity, CallbackInfo ci, float f, float f1, float f2) {
//        CombatData.getCap(this).setCachedCooldown(f2);
//    } //Mohist why

    @Redirect(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource customDamageSource(DamageSources instance, Player player) {
        CombatDamageSource d = new CombatDamageSource(player).setDamageDealer(player.getMainHandItem()).setAttackingHand(CombatData.getCap(player).isOffhandAttack() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).setProcAttackEffects(true).setProcNormalEffects(true).setDamageTyping(CombatDamageSource.TYPE.PHYSICAL);
        ds = d;
        return d;
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;fireCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;")
    )
    private CriticalHitEvent storeCritResult(CriticalHitEvent original, @Local DamageSource source) {
        if(source instanceof CombatDamageSource cds){
            cds.setCrit(original.isCriticalHit());
            cds.setCritDamage(original.getDamageMultiplier());
        }
        return original;
    }

    @Inject(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void stats(Entity targetEntity, CallbackInfo ci) {
        targetEntity.invulnerableTime = 0;
        if (targetEntity instanceof LivingEntity) {
            ((LivingEntity) targetEntity).hurtTime = ((LivingEntity) targetEntity).hurtDuration = 0;
        }
    }

    @Redirect(method = "attack",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void mark(LivingEntity livingEntity, double strength, double ratioX, double ratioZ) {
        //fixme this only happens if the item has knockback enchant
        MeleeKnockbackEvent mke = new MeleeKnockbackEvent(this, ds, livingEntity, strength, ratioX, ratioZ);
        NeoForge.EVENT_BUS.post(mke);
        livingEntity.knockback(mke.getStrength(), mke.getRatioX(), mke.getRatioZ());
    }
}
