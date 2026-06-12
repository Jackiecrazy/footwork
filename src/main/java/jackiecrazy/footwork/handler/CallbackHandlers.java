package jackiecrazy.footwork.handler;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.event.*;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Footwork.MODID)
public class CallbackHandlers {
    @SubscribeEvent
    public static void callback(LivingAttackEvent e) {
        if (!e.getEntity().isEffectiveAi()) return;
        final ArgumentContext addtlctx = new ArgumentContext(null, e.getEntity()).addContext("target", e.getEntity()).addContext("attacker", e.getSource().getEntity()).addContext("proxy", e.getSource().getDirectEntity()).addContext("amount", e.getAmount()).addContext("source", e.getSource());
        ActionData.getCap(e.getEntity()).triggerCallback("attacked", addtlctx);
        if (e.getSource().getEntity() != null) {
            ActionData.getCap(e.getSource().getEntity()).triggerCallback("attack_other", addtlctx);
        }
        if (e.getSource().getDirectEntity() != null && e.getSource().getDirectEntity() != e.getSource().getEntity()) {
            ActionData.getCap(e.getSource().getDirectEntity()).triggerCallback("attack_other", addtlctx);
        }
        cancelEvent(e);
    }

    @SubscribeEvent
    public static void callback(LivingHurtEvent e) {
        final LivingEntity ent = e.getEntity();
        if (!ent.isEffectiveAi()) return;
        ent.getPersistentData().putFloat("amount", e.getAmount());
        final ArgumentContext addtlctx = new ArgumentContext(null, ent).addContext("target", ent).addContext("attacker", e.getSource().getEntity()).addContext("proxy", e.getSource().getDirectEntity()).addContext("amount", e.getAmount()).addContext("source", e.getSource());
        ActionData.getCap(ent).triggerCallback("hurt", addtlctx);
        if (e.getSource().getEntity() != null) {
            ActionData.getCap(e.getSource().getEntity()).triggerCallback("hurt_other", addtlctx);
        }
        if (e.getSource().getDirectEntity() != null && e.getSource().getDirectEntity() != e.getSource().getEntity()) {
            ActionData.getCap(e.getSource().getDirectEntity()).triggerCallback("hurt_other", addtlctx);
        }
        cancelEvent(e);
        if (ent.getPersistentData().contains("amount")) {
            e.setAmount(ent.getPersistentData().getFloat("amount"));
            ent.getPersistentData().remove("amount");
        }
        if (e.getAmount() <= 0) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void callback(LivingDamageEvent e) {
        final LivingEntity ent = e.getEntity();
        if (!ent.isEffectiveAi()) return;
        ent.getPersistentData().putFloat("amount", e.getAmount());
        final ArgumentContext addtlctx = new ArgumentContext(null, ent).addContext("target", ent).addContext("attacker", e.getSource().getEntity()).addContext("proxy", e.getSource().getDirectEntity()).addContext("amount", e.getAmount()).addContext("source", e.getSource());
        ActionData.getCap(ent).triggerCallback("damaged", addtlctx);
        if (e.getSource().getEntity() != null) {
            ActionData.getCap(e.getSource().getEntity()).triggerCallback("damage_other", addtlctx);
        }
        if (e.getSource().getDirectEntity() != null && e.getSource().getDirectEntity() != e.getSource().getEntity()) {
            ActionData.getCap(e.getSource().getDirectEntity()).triggerCallback("damage_other", addtlctx);
        }
        cancelEvent(e);
        if (ent.getPersistentData().contains("amount")) {
            e.setAmount(ent.getPersistentData().getFloat("amount"));
            ent.getPersistentData().remove("amount");
        }
        if (e.getAmount() <= 0) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void callback(LivingDeathEvent e) {
        if (!e.getEntity().isEffectiveAi()) return;
        final ArgumentContext addtlctx = new ArgumentContext(null, e.getEntity()).addContext("target", e.getEntity()).addContext("attacker", e.getSource().getEntity()).addContext("proxy", e.getSource().getDirectEntity()).addContext("source", e.getSource());
        ActionData.getCap(e.getEntity()).triggerCallback("killed", addtlctx);
        if (e.getSource().getEntity() != null) {
            ActionData.getCap(e.getSource().getEntity()).triggerCallback("kill_other", addtlctx);
        }
        if (e.getSource().getDirectEntity() != null && e.getSource().getDirectEntity() != e.getSource().getEntity()) {
            ActionData.getCap(e.getSource().getDirectEntity()).triggerCallback("kill_other", addtlctx);
        }
        cancelEvent(e);
    }

    @SubscribeEvent
    public static void callback(ConsumePostureEvent e) {
        final LivingEntity ent = e.getEntity();
        if (!ent.isEffectiveAi()) return;
        ent.getPersistentData().putFloat("amount", e.getAmount());
        final ArgumentContext addtlctx = new ArgumentContext(null, ent).addContext("target", ent).addContext("attacker", e.getAttacker()).addContext("amount", e.getAmount());
        ActionData.getCap(ent).triggerCallback("take_posture", addtlctx);
        if (e.getAttacker() != null) {
            ActionData.getCap(e.getAttacker()).triggerCallback("deal_posture", addtlctx);
        }
        cancelEvent(e);
        if (ent.getPersistentData().contains("amount")) {
            e.setAmount(ent.getPersistentData().getFloat("amount"));
            ent.getPersistentData().remove("amount");
        }
    }

    @SubscribeEvent
    public static void callback(ConsumeSpiritEvent e) {
        final LivingEntity ent = e.getEntity();
        if (!ent.isEffectiveAi()) return;
        ent.getPersistentData().putFloat("amount", e.getAmount());
        final ArgumentContext addtlctx = new ArgumentContext(null, ent).addContext("target", ent).addContext("amount", e.getAmount());
        ActionData.getCap(ent).triggerCallback("consume_spirit", addtlctx);
        cancelEvent(e);

        if (ent.getPersistentData().contains("amount")) {
            e.setAmount(ent.getPersistentData().getFloat("amount"));
            ent.getPersistentData().remove("amount");
        }
    }

    @SubscribeEvent
    public static void callback(GainSpiritEvent e) {
        final LivingEntity ent = e.getEntity();
        if (!ent.isEffectiveAi()) return;
        ent.getPersistentData().putFloat("amount", e.getQuantity());
        final ArgumentContext addtlctx = new ArgumentContext(null, ent).addContext("amount", e.getQuantity());
        ActionData.getCap(ent).triggerCallback("gain_spirit", addtlctx);
        cancelEvent(e);

        if (ent.getPersistentData().contains("amount")) {
            e.setQuantity(ent.getPersistentData().getFloat("amount"));
            ent.getPersistentData().remove("amount");
        }
    }

    @SubscribeEvent
    public static void callback(GainPostureEvent e) {
        final LivingEntity ent = e.getEntity();
        if (!ent.isEffectiveAi()) return;
        ent.getPersistentData().putFloat("amount", e.getQuantity());
        final ArgumentContext addtlctx = new ArgumentContext(null, ent).addContext("amount", e.getQuantity());
        ActionData.getCap(ent).triggerCallback("gain_posture", addtlctx);
        cancelEvent(e);

        if (ent.getPersistentData().contains("amount")) {
            e.setQuantity(ent.getPersistentData().getFloat("amount"));
            ent.getPersistentData().remove("amount");
        }
    }

    @SubscribeEvent
    public static void callback(ProjectileImpactEvent e) {
        final Projectile p = e.getProjectile();
        Entity actOn = e.getRayTraceResult().getType() == HitResult.Type.ENTITY ? ((EntityHitResult) e.getRayTraceResult()).getEntity() : p;
        final ArgumentContext addtlctx = new ArgumentContext(null, actOn).addContext("projectile", p).addContext("shooter", p.getOwner());
        ActionData.getCap(p).triggerCallback("shot_other", addtlctx);
        if (actOn != p)
            ActionData.getCap(actOn).triggerCallback("shot", addtlctx);
        if (p.getOwner() != null) {
            ActionData.getCap(p.getOwner()).triggerCallback("shot_other", addtlctx);
        }
        cancelEvent(e);
    }

    @SubscribeEvent
    public static void callback(EntityJoinLevelEvent e) {
        final Entity ent = e.getEntity();
        if (!ent.isEffectiveAi()) return;
        final ArgumentContext addtlctx = new ArgumentContext(null, ent);
        if (ent instanceof Projectile p && p.getOwner() != null) {
            ActionData.getCap(p.getOwner()).triggerCallback("shoot_projectile", addtlctx);
        }
        cancelEvent(e);
    }

    private static void cancelEvent(EntityEvent e) {
        if (e.isCancelable() && e.getEntity().getPersistentData().getInt("cancel_event") == 1) e.setCanceled(true);
        e.getEntity().getPersistentData().remove("cancel_event");
    }

    @SubscribeEvent
    public static void callback(MobEffectEvent.Added e) {
        final ArgumentContext addtlctx = new ArgumentContext(null, e.getEntity()).addContext("applier", e.getEffectSource()).addContext("potency", e.getEffectInstance().getAmplifier()).addContext("duration", e.getEffectInstance().getDuration()).addContext("effect", ForgeRegistries.MOB_EFFECTS.getKey(e.getEffectInstance().getEffect()));
        if (e.getEffectInstance().getEffect().isBeneficial()) {
            ActionData.getCap(e.getEntity()).triggerCallback("buffed", addtlctx);
            if (e.getEffectSource() != null) ActionData.getCap(e.getEffectSource()).triggerCallback("buff", addtlctx);
        } else {
            ActionData.getCap(e.getEntity()).triggerCallback("debuffed", addtlctx);
            if (e.getEffectSource() != null) ActionData.getCap(e.getEffectSource()).triggerCallback("debuff", addtlctx);
        }
        cancelEvent(e);
    }

    @SubscribeEvent
    public static void callback(MobEffectEvent.Expired e) {
        final ArgumentContext addtlctx = new ArgumentContext(null, e.getEntity()).addContext("potency", e.getEffectInstance().getAmplifier()).addContext("duration", e.getEffectInstance().getDuration()).addContext("effect", ForgeRegistries.MOB_EFFECTS.getKey(e.getEffectInstance().getEffect()));
        if (e.getEffectInstance().getEffect().isBeneficial()) {
            ActionData.getCap(e.getEntity()).triggerCallback("buff_expire", addtlctx);
        } else {
            ActionData.getCap(e.getEntity()).triggerCallback("debuff_expire", addtlctx);
        }
        cancelEvent(e);
    }

    @SubscribeEvent
    public static void callback(DodgeEvent e) {
        final LivingEntity ent = e.getEntity();
        ent.getPersistentData().putDouble("dodge_force", e.getForce());
        final ArgumentContext addtlctx = new ArgumentContext(null, ent).addContext("dodge_force", e.getForce());
        ActionData.getCap(ent).triggerCallback("dodge", addtlctx);
        if (ent.getPersistentData().contains("dodge_force")) {
            e.setForce((float) ent.getPersistentData().getDouble("dodge_force"));
            ent.getPersistentData().remove("dodge_force");
        }
        cancelEvent(e);
    }
}
