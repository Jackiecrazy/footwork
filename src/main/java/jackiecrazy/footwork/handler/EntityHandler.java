package jackiecrazy.footwork.handler;

import com.mojang.math.Vector3f;
import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.weaponry.CombatManipulator;
import jackiecrazy.footwork.capability.weaponry.ICombatItemCapability;
import jackiecrazy.footwork.client.particle.FootworkParticles;
import jackiecrazy.footwork.client.particle.ScalingParticleType;
import jackiecrazy.footwork.entity.ai.CompelledVengeanceGoal;
import jackiecrazy.footwork.entity.ai.FearGoal;
import jackiecrazy.footwork.entity.ai.NoGoal;
import jackiecrazy.footwork.potion.FootworkEffects;
import jackiecrazy.footwork.utils.EffectUtils;
import jackiecrazy.footwork.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Footwork.MODID)
public class EntityHandler {

    private static final UUID uuid = UUID.fromString("98c361c7-de32-4f40-b129-d7752bac3712");
    private static final UUID uuid2 = UUID.fromString("98c361c8-de32-4f40-b129-d7752bac3722");

    @SubscribeEvent
    public static void takeThis(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof Mob mob) {
            mob.goalSelector.addGoal(-1, new NoGoal(mob));
            mob.targetSelector.addGoal(-1, new NoGoal(mob));
            if (e.getEntity() instanceof PathfinderMob) {
                PathfinderMob creature = (PathfinderMob) e.getEntity();
                mob.targetSelector.addGoal(0, new CompelledVengeanceGoal(creature));
                mob.targetSelector.addGoal(0, new FearGoal(creature));
            }
        }
    }

    @SubscribeEvent
    public static void tickMobs(LivingEvent.LivingTickEvent e) {
        LivingEntity elb = e.getEntity();
        if (!elb.level.isClientSide) {
            if (elb.hasEffect(FootworkEffects.PETRIFY.get())) {
                elb.setXRot(elb.xRotO);
                elb.setYRot(elb.yRotO);
                elb.yHeadRot = elb.yHeadRotO;
                elb.setDeltaMovement(0, 0, 0);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damageAmp(LivingDamageEvent e) {
        if (e.getSource() instanceof CombatDamageSource cds) {
            e.setAmount(e.getAmount() * cds.getMultiplier());
        }
    }

    @SubscribeEvent
    public static void auxEffects(MobEffectEvent.Added e) {
        if (e.getEffectInstance().getEffect() == FootworkEffects.CONFUSION.get()) {
            //accompanied by nausea
            e.getEntity().addEffect(new MobEffectInstance(MobEffects.CONFUSION, e.getEffectInstance().getDuration(), e.getEffectInstance().getAmplifier()));
        }
    }

    static boolean isPhysicalAttack(DamageSource s) {
        if (s instanceof CombatDamageSource cds) {
            return cds.getDamageTyping() == CombatDamageSource.TYPE.PHYSICAL;
        }
        return !s.isExplosion() && !s.isFire() && !s.isMagic() && !s.isBypassArmor();
    }

    static boolean isMeleeAttack(DamageSource s) {
        if (s instanceof CombatDamageSource) {
            return ((CombatDamageSource) s).canProcAutoEffects();
        }
        //TODO does this break anything?
        return s.getEntity() != null && s.getEntity() == s.getDirectEntity() && !s.isExplosion() && !s.isProjectile();//!s.isFire() && !s.isMagic() &&
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void pain(LivingHurtEvent e) {
        LivingEntity uke = e.getEntity();
        LivingEntity kek = null;
        DamageSource ds = e.getSource();
        if (uke.hasEffect(FootworkEffects.VULNERABLE.get()) && !isPhysicalAttack(ds))
            e.setAmount(e.getAmount() + uke.getEffect(FootworkEffects.VULNERABLE.get()).getAmplifier() + 1);
        if (ds.getDirectEntity() instanceof LivingEntity) {
            kek = (LivingEntity) ds.getDirectEntity();
        }
        uke.getAttribute(Attributes.ARMOR).removeModifier(uuid);
        uke.getAttribute(Attributes.ARMOR).removeModifier(uuid2);
        if (ds instanceof CombatDamageSource) {
            float mult = -((CombatDamageSource) ds).getArmorReductionPercentage();
            if (mult != 0) {
                AttributeModifier armor = new AttributeModifier(uuid2, "temporary armor multiplier", mult, AttributeModifier.Operation.MULTIPLY_TOTAL);
                uke.getAttribute(Attributes.ARMOR).addTransientModifier(armor);
            }
        }
        ItemStack ukemain = uke.getMainHandItem();
        ItemStack ukeoff = uke.getOffhandItem();
        if (ukemain.getCapability(CombatManipulator.CAP).isPresent()) {
            ICombatItemCapability icic = ukemain.getCapability(CombatManipulator.CAP).resolve().get();
            e.setAmount(icic.onBeingHurt(e.getSource(), uke, ukemain, e.getAmount()));
        }
        if (ukeoff.getCapability(CombatManipulator.CAP).isPresent()) {
            ICombatItemCapability icic = ukeoff.getCapability(CombatManipulator.CAP).resolve().get();
            e.setAmount(icic.onBeingHurt(e.getSource(), uke, ukeoff, e.getAmount()));
        }
        if (ds.getEntity() instanceof LivingEntity seme) {
            if (seme.getMainHandItem().getCapability(CombatManipulator.CAP).isPresent()) {
                ICombatItemCapability icic = seme.getMainHandItem().getCapability(CombatManipulator.CAP).resolve().get();
                e.setAmount(icic.hurtStart(e.getSource(), seme, uke, seme.getMainHandItem(), e.getAmount()) * icic.damageMultiplier(seme, uke, seme.getMainHandItem()));
                AttributeModifier armor = new AttributeModifier(uuid, "temporary armor removal", -icic.armorIgnoreAmount(e.getSource(), seme, uke, seme.getMainHandItem(), e.getAmount()), AttributeModifier.Operation.ADDITION);
                uke.getAttribute(Attributes.ARMOR).addTransientModifier(armor);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void tanky(LivingDamageEvent e) {
        final LivingEntity uke = e.getEntity();
        uke.getAttribute(Attributes.ARMOR).removeModifier(uuid);
        uke.getAttribute(Attributes.ARMOR).removeModifier(uuid2);
        if (e.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity seme = ((LivingEntity) e.getSource().getEntity());
            if (isMeleeAttack(e.getSource())) {
                ItemStack sememain = seme.getMainHandItem();
                if (sememain.getCapability(CombatManipulator.CAP).isPresent()) {
                    ICombatItemCapability icic = sememain.getCapability(CombatManipulator.CAP).resolve().get();
                    e.setAmount(icic.damageStart(e.getSource(), seme, uke, sememain, e.getAmount()));
                }
                ItemStack ukemain = uke.getMainHandItem();
                ItemStack ukeoff = uke.getOffhandItem();
                if (ukemain.getCapability(CombatManipulator.CAP).isPresent()) {
                    ICombatItemCapability icic = ukemain.getCapability(CombatManipulator.CAP).resolve().get();
                    e.setAmount(icic.onBeingDamaged(e.getSource(), uke, ukemain, e.getAmount()));
                }
                if (ukeoff.getCapability(CombatManipulator.CAP).isPresent()) {
                    ICombatItemCapability icic = ukeoff.getCapability(CombatManipulator.CAP).resolve().get();
                    e.setAmount(icic.onBeingDamaged(e.getSource(), uke, ukeoff, e.getAmount()));
                }
            }
        }
        if (e.getAmount() <= 0) e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void udedlol(LivingDamageEvent e) {
        LivingEntity uke = e.getEntity();
        uke.removeEffect(FootworkEffects.DISTRACTION.get());
        uke.removeEffect(FootworkEffects.FEAR.get());
        uke.removeEffect(FootworkEffects.SLEEP.get());
        //ParticleUtils.playBonkParticle(uke.level, uke.getEyePosition(), 1, 1, 20, Color.RED);
    }
}
