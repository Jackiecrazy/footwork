package jackiecrazy.footwork.handler;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.api.FootworkDamageArchetype;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.weaponry.CombatManipulator;
import jackiecrazy.footwork.capability.weaponry.ICombatItemCapability;
import jackiecrazy.footwork.entity.ai.CompelledVengeanceGoal;
import jackiecrazy.footwork.entity.ai.FearGoal;
import jackiecrazy.footwork.entity.ai.NoGoal;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Footwork.MODID)
public class EntityHandler {

    private static final ResourceLocation pierce = ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "temp_armor_off");
    private static final ResourceLocation armorMult = ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "temp_armor_mult");

    @SubscribeEvent
    public static void auxEffects(MobEffectEvent.Added e) {
        if (e.getEffectInstance().getEffect() == FootworkEffects.CONFUSION.get()) {
            //accompanied by nausea
            e.getEntity().addEffect(new MobEffectInstance(MobEffects.CONFUSION, e.getEffectInstance().getDuration(), e.getEffectInstance().getAmplifier()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damageAmp(LivingDamageEvent.Pre e) {
        if (e.getSource() instanceof CombatDamageSource cds) {
            e.setNewDamage(e.getNewDamage() * cds.getMultiplier());
        }
    }

    static boolean isMeleeAttack(DamageSource s) {
        return ((CombatDamageSource) s).canProcAutoEffects();
        //TODO does this break anything?
        //return s.getEntity() != null && s.getEntity() == s.getDirectEntity() && !s.is(DamageTypeTags.IS_EXPLOSION) && !s.is(DamageTypeTags.IS_PROJECTILE);//!s.isFire() && !s.isMagic() &&
    }

    static boolean isPhysicalAttack(DamageSource s) {
        return ((CombatDamageSource) s).getDamageTyping() == FootworkDamageArchetype.PHYSICAL;
        //return !s.is(DamageTypeTags.IS_EXPLOSION) && !s.is(DamageTypeTags.IS_FIRE) && !s.is(DamageTypeTags.WITCH_RESISTANT_TO) && !s.is(DamageTypeTags.BYPASSES_ARMOR);
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
        uke.getAttribute(Attributes.ARMOR).removeModifier(pierce);
        uke.getAttribute(Attributes.ARMOR).removeModifier(armorMult);
        if (ds instanceof CombatDamageSource cds) {
            float mult = -cds.getArmorReductionPercentage();
            if (mult != 0) {
                AttributeModifier armor = new AttributeModifier(armorMult, mult, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                uke.getAttribute(Attributes.ARMOR).addTransientModifier(armor);
            }
        }
        ItemStack ukemain = uke.getMainHandItem();
        ItemStack ukeoff = uke.getOffhandItem();
        ICombatItemCapability icic = ukemain.getCapability(CombatManipulator.CAP);
        if (icic != null)
            e.setAmount(icic.onBeingHurt(e.getSource(), uke, ukemain, e.getAmount()));
        icic = ukemain.getCapability(CombatManipulator.CAP);
        if (icic != null)
            e.setAmount(icic.onBeingHurt(e.getSource(), uke, ukeoff, e.getAmount()));
        if (ds.getEntity() instanceof LivingEntity seme) {
            icic = seme.getMainHandItem().getCapability(CombatManipulator.CAP);
            if (icic != null) {
                e.setAmount(icic.hurtStart(e.getSource(), seme, uke, seme.getMainHandItem(), e.getAmount()) * icic.damageMultiplier(seme, uke, seme.getMainHandItem()));
                AttributeModifier armor = new AttributeModifier(pierce,  -icic.armorIgnoreAmount(e.getSource(), seme, uke, seme.getMainHandItem(), e.getAmount()), AttributeModifier.Operation.ADD_VALUE);
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
    public static void tanky(LivingDamageEvent.Pre e) {
        final LivingEntity uke = e.getEntity();
        uke.getAttribute(Attributes.ARMOR).removeModifier(pierce);
        uke.getAttribute(Attributes.ARMOR).removeModifier(armorMult);
        if (e.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity seme = ((LivingEntity) e.getSource().getEntity());
            if (isMeleeAttack(e.getSource())) {
                ItemStack sememain = seme.getMainHandItem();
                ICombatItemCapability icic = sememain.getCapability(CombatManipulator.CAP);
                if (icic != null) {
                    e.setNewDamage(icic.damageStart(e.getSource(), seme, uke, sememain, e.getNewDamage()));
                }
                ItemStack ukemain = uke.getMainHandItem();
                ItemStack ukeoff = uke.getOffhandItem();
                icic = ukemain.getCapability(CombatManipulator.CAP);
                if (icic != null) {
                    e.setNewDamage(icic.onBeingDamaged(e.getSource(), uke, ukemain, e.getNewDamage()));
                }
                icic = ukeoff.getCapability(CombatManipulator.CAP);
                if (icic != null) {
                    e.setNewDamage(icic.onBeingDamaged(e.getSource(), uke, ukeoff, e.getNewDamage()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void tickMobs(EntityTickEvent.Pre e) {//todo is this pre or post? I'm guessing pre
        Entity en = e.getEntity();
        if (en instanceof LivingEntity elb)
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
        if (e.getSource() instanceof CombatDamageSource cds) {
            if (cds.getDamageTyping() == FootworkDamageArchetype.TRUE)
                //true damage means true damage, dammit!
                e.setNewDamage(e.getContainer().getOriginalDamage());
        }
        //ParticleUtils.playSweepParticle(FootworkParticles.LINE.get(), uke, uke.getPosition(0.5f), 5, 1, Color.RED, 1);
    }
}
