package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.event.MeleeKnockbackEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
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

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

//    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
//            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/player/PlayerEntity;resetCooldown()V"))
//    private void noReset(Entity targetEntity, CallbackInfo ci, float f, float f1, float f2) {
//        CombatData.getCap(this).setCachedCooldown(f2);
//    } //Mohist why



    @Inject(method = "attack", locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void store(Entity targetEntity, CallbackInfo ci, float f, float f1, float f2, boolean flag, boolean flag1, float i, boolean flag2, CriticalHitEvent hitResult, boolean flag3, double d0, float f4, boolean flag4, int j, Vec3 vector3d) {
        tempCrit = flag2;
        tempCdmg = hitResult == null ? 1 : hitResult.getDamageModifier();
    }

    @Redirect(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource customDamageSource(Player player) {
        CombatDamageSource d = new CombatDamageSource("player", player).setDamageDealer(player.m_21205_()).setAttackingHand(CombatData.getCap(player).isOffhandAttack() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).setProcAttackEffects(true).setProcNormalEffects(true).setCrit(tempCrit).setCritDamage(tempCdmg).setDamageTyping(CombatDamageSource.TYPE.PHYSICAL);
        ds = d;
        return d;
    }

    @Inject(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void stats(Entity targetEntity, CallbackInfo ci) {
        targetEntity.f_19802_ = 0;
        if (targetEntity instanceof LivingEntity) {
            ((LivingEntity) targetEntity).f_20916_ = ((LivingEntity) targetEntity).f_20917_ = 0;
        }
    }

    @Redirect(method = "attack",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void mark(LivingEntity livingEntity, double strength, double ratioX, double ratioZ) {
        MeleeKnockbackEvent mke = new MeleeKnockbackEvent(this, ds, livingEntity, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post(mke);
        livingEntity.m_147240_(mke.getStrength(), mke.getRatioX(), mke.getRatioZ());
    }
}
