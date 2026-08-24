package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.api.FootworkDamageArchetype;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.event.MeleeKnockbackEvent;
import jackiecrazy.footwork.event.MeleeDamageSourceEvent;
import jackiecrazy.footwork.utils.GeneralUtils;
import jackiecrazy.footwork.utils.MovementUtils;
import net.minecraft.client.animation.definitions.WardenAnimation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    private static boolean tempCrit;
    private static float tempCdmg;
    private static DamageSource ds;
    private static Entity hitting;
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

//    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
//            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/player/PlayerEntity;resetCooldown()V"))
//    private void noReset(Entity targetEntity, CallbackInfo ci, float f, float f1, float f2) {
//        CombatData.getCap(this).setCachedCooldown(f2);
//    } //Mohist why

    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
    private void store(Entity p_36347_,
                       CallbackInfo ci,
                       float f,
                       float f1,
                       float f2,
                       boolean flag,
                       boolean flag1,
                       float i,
                       boolean flag2,
                       CriticalHitEvent hitResult,
                       boolean flag3,
                       double d0,
                       float f4,
                       boolean flag4,
                       int j) {
        tempCrit = flag2;
        tempCdmg = hitResult == null ? 1 : hitResult.getDamageModifier();
        hitting=p_36347_;
    }

    @Redirect(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource customDamageSource(DamageSources instance, Player player) {
        GeneralUtils.kbHandled = false;
        ds = null;
        if (GeneralUtils.player_ds_override != null) {
            ds = GeneralUtils.player_ds_override;
            //GeneralUtils.player_ds_override = null;
        } else {
            ds = new CombatDamageSource(player).setDamageDealer(getMainHandItem()).setAttackingHand(CombatData.getCap(this).isOffhandAttack() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).setProcAttackEffects(true).setProcNormalEffects(true).flagBreach(false).setDamageTyping(FootworkDamageArchetype.PHYSICAL);
        }
        if(ds instanceof CombatDamageSource cds)
            cds.setCrit(tempCrit).setCritDamage(tempCdmg);//everyone needs these tags
        MeleeDamageSourceEvent event= new MeleeDamageSourceEvent(player, hitting, ds);
        MinecraftForge.EVENT_BUS.post(event);
        DamageSource ret = event.getDamageSource();
        if(ret==null) {
            Footwork.LOGGER.fatal("damage source is null! Aborting all custom changes!");
            ds=new CombatDamageSource(player).setDamageDealer(getMainHandItem()).setAttackingHand(CombatData.getCap(this).isOffhandAttack() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).setProcAttackEffects(true).setProcNormalEffects(true).flagBreach(false).setDamageTyping(FootworkDamageArchetype.PHYSICAL);
            return ds;
        }
        return ret;
    }

//    @ModifyArg(
//            method = "attack",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
//            ),
//            index = 0   // the DamageSource is always argument 0 of hurt()
//    )
//    private DamageSource replaceDamageSourceForHurt(DamageSource original) {
//        // same code as before
//        CombatDamageSource ds = new CombatDamageSource(this); // 'this' = player
//        ds.setDamageDealer(getMainHandItem()).setAttackingHand(CombatData.getCap(this).isOffhandAttack() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).setProcAttackEffects(true).setProcNormalEffects(true).setCrit(tempCrit).flagBreach(false).setCritDamage(tempCdmg).setDamageTyping(FootworkDamageArchetype.PHYSICAL);
//        return ds;
//    }

    @Inject(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
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
        MeleeKnockbackEvent event = new MeleeKnockbackEvent(this, ds, livingEntity, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post(event);
        //fixme this overwrites the resolution from mixinAttackSpeed
        if(ds instanceof CombatDamageSource cds && cds.getKnockbackVector()!=null){
            strength = event.getStrength();
            ratioX = event.getRatioX();
            ratioZ = event.getRatioZ();
            strength *= 1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            if (strength != 0) {
                livingEntity.hasImpulse = true;
                Vec3 currentMovement = livingEntity.getDeltaMovement();
                Vec3 originalVector = new Vec3(ratioX, 0, ratioZ);
                final Vec3 knockbackVector = cds.getKnockbackVector();
                Vec3 addedMovement = MovementUtils.resolveVelocity(originalVector, knockbackVector).normalize().scale(strength);
                livingEntity.setDeltaMovement(currentMovement.x / 2.0D + addedMovement.x, currentMovement.y / 2.0D + addedMovement.y, currentMovement.z / 2.0D + addedMovement.z);
            }
        }
        else livingEntity.knockback(event.getStrength(), event.getRatioX(), event.getRatioZ());
        GeneralUtils.kbHandled = true;
    }

    @Inject(method = "attack",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/player/Player;setLastHurtMob(Lnet/minecraft/world/entity/Entity;)V"))
    private void kb(Entity e, CallbackInfo ci) {
        if (!GeneralUtils.kbHandled && e instanceof LivingEntity livingEntity) {
            MeleeKnockbackEvent mke = new MeleeKnockbackEvent(this, ds, livingEntity, 0.4, (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
            MinecraftForge.EVENT_BUS.post(mke);

        }
    }
    @Inject(method = "attack",
            at = @At(value = "RETURN"))
    private void resetDS(Entity p_36347_, CallbackInfo ci) {
        GeneralUtils.player_ds_override=null;
    }
}
