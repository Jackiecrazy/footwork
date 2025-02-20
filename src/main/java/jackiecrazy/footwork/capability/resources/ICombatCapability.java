package jackiecrazy.footwork.capability.resources;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public interface ICombatCapability {
    //might, spirit, posture, combo
    //might cooldown, spirit cooldown, posture cooldown, combo grace period
    //stagger timer, stagger counter for downed attacks
    //offhand cooldown, shield parry time, sidestep/roll timer
    //set, get, increment/decrement, consume (resource only)
    //is offhand attack, combat mode
    //shatter, shatter cooldown
    void updateDefenselessStatus();

    float getMaxMight();

    float getMight();

    void setMight(float amount);

    float addMight(float amount);

    default boolean consumeMight(float amount) {
        return consumeMight(amount, 0);
    }

    boolean consumeMight(float amount, float above);

    int getMightGrace();

    void setMightGrace(int amount);

    float getMaxSpirit();

    float getSpirit();

    void setSpirit(float amount);

    float addSpirit(float amount);

    default boolean consumeSpirit(float amount) {
        return consumeSpirit(amount, 0);
    }

    boolean consumeSpirit(float amount, float above);

    int getSpiritGrace();

    void setSpiritGrace(int amount);

    float getMaxPosture();

    float getPosture();

    void setPosture(float amount);

    float addPosture(float amount);

    default boolean doConsumePosture(float amount) {
        return consumePosture(amount, 0) == 0;
    }

    default float consumePosture(float amount) {
        return consumePosture(amount, 0);
    }

    default float consumePosture(LivingEntity attacker, float amount) {
        return consumePosture(attacker, amount, 0);
    }

    default float consumePosture(float amount, float above) {
        return consumePosture(null, amount, above, false);
    }

    default float consumePosture(LivingEntity attacker, float amount, float above) {
        return consumePosture(attacker, amount, above, false);
    }

    float consumePosture(LivingEntity assailant, float amount, float above, boolean force);

    int getPostureGrace();

    void setPostureGrace(int amount);

    int getMaxStunTime();

    int getStunTime();

    void stun(int time);

    default boolean isVulnerable() {
        return isStunned() || isKnockedDown() || isExposed();
    }

    int getMaxKnockdownTime();

    int getKnockdownTime();

    void knockdown(int time);

    boolean isStunned();

    boolean isKnockedDown();

    int getFractureCount();

    int getFractureCount(LivingEntity appliedBy);

    /**
     * @return the entity id-fracture marks inflicted by it
     * edge case behavior: revenge killing an entity will clear all invalid fracture marks, for dimension reasons
     */
    HashMap<UUID, Integer> getFractureList();

    boolean addFracture(@Nullable LivingEntity source, int amount);

    void clearFracture(@Nullable LivingEntity of, boolean clearInvalid);

    float getMaxFracture();

    int getMaxExposeTime();

    int getExposeTime();

    void expose(int time);

    boolean isExposed();

    float getRank();

    void setRank(float amount);

    default int getComboRank() {
        float workingCombo = getRank();
        if (workingCombo >= 9)
            return 7;//SSStylish!
        if (workingCombo >= 6) {
            return 6;//SShowtime!
        }
        if (workingCombo >= 4) {
            return 5;//Sweet!
        }
        return (int) workingCombo;//all other ranks
    }

    void setAdrenalineCooldown(int amount);

    boolean halvedAdrenaline();

    float addRank(float amount);

    default boolean consumeRank(float amount) {
        return consumeRank(amount, 0);
    }

    boolean consumeRank(float amount, float above);

    int getOffhandCooldown();

    void setOffhandCooldown(int amount);

    /**
     * for the sake of convenience, positive is subject to cooldown and negatives are free
     */
    int getRollTime();

    void setRollTime(int amount);

    void decrementRollTime(int amount);

    boolean isOffhandAttack();

    void setOffhandAttack(boolean off);

    boolean isCombatMode();

    void toggleCombatMode(boolean on);

    int getHandBind(InteractionHand h);

    void setHandBind(InteractionHand h, int amount);

    boolean consumeEvade();

    int getEvade();

    void setEvade(int value);

    float getCachedCooldown();

    void setCachedCooldown(float value);

    int getForcedSweep();

    void setForcedSweep(int angle);

    void clientTick();

    void serverTick();

    void sync();

    ItemStack getTempItemStack();

    void setTempItemStack(ItemStack is);

    int getParryingTick();//hey, it's useful for future "smart" entities as well.

    void setParryingTick(int parrying);

    int getSweepTick();

    void setSweepTick(int tick);

    boolean isValid();

    Vec3 getMotionConsistently();//I can't believe I have to do this.

    CompoundTag write();

    void read(CompoundTag tag);

    void addRangedMight(boolean pass);

    boolean isStaggeringStrike();

    int getRetina();

    float visionRange();
}
