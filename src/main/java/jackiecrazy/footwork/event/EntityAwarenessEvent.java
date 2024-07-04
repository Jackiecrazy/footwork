package jackiecrazy.footwork.event;

import jackiecrazy.footwork.utils.StealthUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

/**
 * The superclass indiscriminately fires on checking for awareness. Please use it for general awareness overrides and bypasses.
 * If you want to perform an action on backstab, please use the subclass. It is fired once by Cloak and Dagger upon LivingHurtEvent.
 * Note that LivingHurtEvent, due to code constraints, will fire both the superclass and the subclass.
 * Once again, please only use the superclass for changing awareness outcomes instead of operating on the involved entities.
 */
public class EntityAwarenessEvent extends LivingEvent {
    private final StealthUtils.Awareness originalStatus;
    private LivingEntity attacker;
    private StealthUtils.Awareness status;

    public EntityAwarenessEvent(LivingEntity entity, LivingEntity attacker, StealthUtils.Awareness originally) {
        super(entity);
        this.attacker = attacker;
        originalStatus = status = originally;
    }

    public StealthUtils.Awareness getAwareness() {
        return status;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return attacker;
    }

    public void setAwareness(StealthUtils.Awareness newAwareness) {
        status = newAwareness;
    }

    public StealthUtils.Awareness getOriginalAwareness() {
        return originalStatus;
    }

    public static class Attack extends EntityAwarenessEvent {
        private final DamageSource ds;
        public Attack(LivingEntity entity, LivingEntity attacker, StealthUtils.Awareness originally, DamageSource ds) {
            super(entity, attacker, originally);
            this.ds = ds;
        }

        public DamageSource getSource() {
            return ds;
        }
    }

    public static class Hurt extends EntityAwarenessEvent {
        private DamageSource ds;

        public double getDistractedMultiplier() {
            return distractedMult;
        }

        public void setDistractedMultiplier(double distractedMult) {
            this.distractedMult = distractedMult;
        }

        public double getUnawareMultiplier() {
            return unawareMult;
        }

        public void setUnawareMultiplier(double unawareMult) {
            this.unawareMult = unawareMult;
        }

        private double distractedMult;
        private double unawareMult;

        public double getAlertMultiplier() {
            return alertMultiplier;
        }

        public void setAlertMultiplier(double alertMultiplier) {
            this.alertMultiplier = alertMultiplier;
        }

        private double alertMultiplier;

        public Hurt(LivingEntity entity, LivingEntity attacker, StealthUtils.Awareness originally, DamageSource ds) {
            super(entity, attacker, originally);
            this.ds = ds;
        }

        public DamageSource getSource() {
            return ds;
        }
    }
}
