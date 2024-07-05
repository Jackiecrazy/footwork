package jackiecrazy.footwork.handler;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.weaponry.CombatManipulator;
import jackiecrazy.footwork.capability.weaponry.ICombatItemCapability;
import jackiecrazy.footwork.client.particle.FootworkParticles;
import jackiecrazy.footwork.entity.ai.CompelledVengeanceGoal;
import jackiecrazy.footwork.entity.ai.FearGoal;
import jackiecrazy.footwork.entity.ai.NoGoal;
import jackiecrazy.footwork.potion.FootworkEffects;
import jackiecrazy.footwork.utils.ParticleUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.UUID;

@EventBusSubscriber(modid = Footwork.MODID)
public class EntityHandler {

    private static final ResourceLocation icic_armor_pierce = ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "custom_armor_pierce");
    private static final ResourceLocation ds_armor_pierce = ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "temp_armor_remove");

    @SubscribeEvent
    public static void auxEffects(MobEffectEvent.Added e) {
        if (e.getEffectInstance().getEffect() == FootworkEffects.CONFUSION) {
            //accompanied by nausea
            e.getEntity().addEffect(new MobEffectInstance(MobEffects.CONFUSION, e.getEffectInstance().getDuration(), e.getEffectInstance().getAmplifier()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damageAmp(LivingDamageEvent.Pre e) {
        DamageContainer dc = e.getContainer();
        if (dc.getSource() instanceof CombatDamageSource cds) {
            dc.setNewDamage(dc.getNewDamage() * cds.getMultiplier());
        }
    }

    private static @Nullable ICombatItemCapability getCapability(ItemStack ukemain) {
        return ukemain.getCapability(CombatManipulator.CAP);
    }

    static boolean isMeleeAttack(DamageSource s) {
        if (s instanceof CombatDamageSource) {
            return ((CombatDamageSource) s).canProcAutoEffects();
        }
        //TODO does this break anything?
        return s.getEntity() != null && s.getEntity() == s.getDirectEntity() && !s.is(DamageTypeTags.IS_EXPLOSION) && !s.is(DamageTypeTags.IS_PROJECTILE);//!s.isFire() && !s.isMagic() &&
    }

    static boolean isPhysicalAttack(DamageSource s) {
        if (s instanceof CombatDamageSource cds) {
            return cds.getDamageTyping() == CombatDamageSource.TYPE.PHYSICAL;
        }
        return !s.is(DamageTypeTags.IS_EXPLOSION) && !s.is(DamageTypeTags.IS_FIRE) && !s.is(DamageTypeTags.WITCH_RESISTANT_TO) && !s.is(DamageTypeTags.BYPASSES_ARMOR);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void pain(LivingIncomingDamageEvent e) {
        LivingEntity uke = e.getEntity();
        LivingEntity kek = null;
        DamageSource ds = e.getSource();
        if (uke.hasEffect(FootworkEffects.VULNERABLE) && !isPhysicalAttack(ds))
            e.setAmount(e.getAmount() + uke.getEffect(FootworkEffects.VULNERABLE).getAmplifier() + 1);
        if (ds.getDirectEntity() instanceof LivingEntity) {
            kek = (LivingEntity) ds.getDirectEntity();
        }
        uke.getAttribute(Attributes.ARMOR).removeModifier(icic_armor_pierce);
        uke.getAttribute(Attributes.ARMOR).removeModifier(ds_armor_pierce);
        if (ds instanceof CombatDamageSource cds) {
            cds.setOriginalDamage(e.getOriginalAmount());
            float mult = -cds.getArmorReductionPercentage();
            if (mult != 0) {
                AttributeModifier armor = new AttributeModifier(ds_armor_pierce, mult, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                uke.getAttribute(Attributes.ARMOR).addTransientModifier(armor);
            }
        }
        ItemStack ukemain = uke.getMainHandItem();
        ItemStack ukeoff = uke.getOffhandItem();
        ICombatItemCapability mainCap = ukemain.getCapability(CombatManipulator.CAP);
        if (mainCap != null) {
            e.setAmount(mainCap.onBeingHurt(e.getContainer(), uke, ukemain));
        }
        ICombatItemCapability offCap = ukeoff.getCapability(CombatManipulator.CAP);
        if (offCap != null) {
            e.setAmount(offCap.onBeingHurt(e.getContainer(), uke, ukeoff));
        }
        if (ds.getEntity() instanceof LivingEntity seme) {
            ICombatItemCapability semCap = seme.getMainHandItem().getCapability(CombatManipulator.CAP);
            if (semCap != null) {
                e.setAmount(semCap.hurtStart(e.getContainer(), seme, uke, seme.getMainHandItem()) * semCap.damageMultiplier(seme, uke, seme.getMainHandItem()));
                AttributeModifier armor = new AttributeModifier(icic_armor_pierce, -semCap.armorIgnoreAmount(e.getContainer(), seme, uke, seme.getMainHandItem()), AttributeModifier.Operation.ADD_VALUE);
                uke.getAttribute(Attributes.ARMOR).addTransientModifier(armor);
            }
        }
    }

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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void tanky(LivingDamageEvent.Pre ent) {
        final LivingEntity uke = ent.getEntity();
        uke.getAttribute(Attributes.ARMOR).removeModifier(icic_armor_pierce);
        uke.getAttribute(Attributes.ARMOR).removeModifier(ds_armor_pierce);
        DamageContainer e = ent.getContainer();
        if (e.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity seme = ((LivingEntity) e.getSource().getEntity());
            if (isMeleeAttack(e.getSource())) {
                ItemStack sememain = seme.getMainHandItem();
                if (sememain.getCapability(CombatManipulator.CAP)!=null) {
                    ICombatItemCapability icic = sememain.getCapability(CombatManipulator.CAP);
                    e.setNewDamage(icic.damageStart(e, seme, uke, sememain));
                }
                ItemStack ukemain = uke.getMainHandItem();
                ItemStack ukeoff = uke.getOffhandItem();
                if (ukemain.getCapability(CombatManipulator.CAP)!=null) {
                    ICombatItemCapability icic = ukemain.getCapability(CombatManipulator.CAP);
                    e.setNewDamage(icic.onBeingDamaged(e, uke, ukemain));
                }
                if (ukeoff.getCapability(CombatManipulator.CAP)!=null) {
                    ICombatItemCapability icic = ukeoff.getCapability(CombatManipulator.CAP);
                    e.setNewDamage(icic.onBeingDamaged(e, uke, ukeoff));
                }
            }
        }
    }

    @SubscribeEvent
    public static void tickMobs(EntityTickEvent.Pre e) {
        if (!(e.getEntity() instanceof LivingEntity elb)) return;
        if (CombatData.getCap(elb).isVulnerable() || elb.hasEffect(FootworkEffects.PETRIFY) || elb.hasEffect(FootworkEffects.SLEEP) || elb.hasEffect(FootworkEffects.PARALYSIS)) {
            elb.setXRot(elb.xRotO);
            elb.setYRot(elb.yRotO);
            elb.yHeadRot = elb.yHeadRotO;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void udedlol(LivingDamageEvent.Pre e) {
        LivingEntity uke = e.getEntity();
        uke.removeEffect(FootworkEffects.DISTRACTION);
        uke.removeEffect(FootworkEffects.FEAR);
        uke.removeEffect(FootworkEffects.SLEEP);
        if (e.getContainer().getSource() instanceof CombatDamageSource cds && cds.getDamageTyping() == CombatDamageSource.TYPE.TRUE) {
            //true damage means true damage, dammit!
            e.getContainer().setNewDamage(cds.getOriginalDamage());
        }
        //ParticleUtils.playSweepParticle(FootworkParticles.LINE.get(), uke, uke.getPosition(0.5f), 5, 1, Color.RED, 1);
    }
}
