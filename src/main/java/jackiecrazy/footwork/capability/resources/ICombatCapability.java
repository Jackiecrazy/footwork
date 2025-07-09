package jackiecrazy.footwork.capability.resources;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface ICombatCapability {
    //adrenaline, spirit (int), posture, rally, trigger bar counter
    //global out of combat cooldown, spirit cooldown, rally timer, combo grace period
    //knockdown timer, stun timer. Don't need to be separate. Use one boolean to store whether the timer is for stunned or knocked down.
    //offhand cooldown, shield parry time, dodge timer, dodge cooldown
    //set, get, increment/decrement, consume (resource only)
    //is offhand attack, combat mode
    //recording time, recorded damage, recorder


    //adrenaline is generated the same way as might, but at a bigger discount because the hard cap is 1.
    //spirit is an int, remember to +1 on roll and refill on iframes.
    //posture is the good ol' posture except it only regenerates on mobs. It's about mid sized.
    //have consume posture not have an above/below flag or any of that crap, only a can_breach that's flagged false if it's a non-trigger player attack
    //while posture is not empty incoming damage is reduced by posture??? How to calculate damage <> posture?
    //on taking a breaching hit to posture, flag stun, which interrupts all AI, cancels all knockback, and records damage?
    //on taking a breaching hit while stunned, flag knockdown, greatly knockback, make entity invulnerable until end.
    //posture is not consumed and regens at a fixed rate when flagged in either condition, but it becomes gray until it's cleared.
    //it takes about 6s to get back to full.
    //on receiving jump input as player, perform circle sweep with knockback and return to mobility at current posture percentage.
    //rally gets set after posture is consumed with a flag to rally (all external sources of damage).
    // It stays at max for half a second, then loses max(1, 1/(10*rally duration)) of its value per tick until it rounds to the true value.
    //split guard frames, dodge frames, iframes, and parry frames.
    //Guard frames are set to a number and decrement, you are considered guarding while it's active. Guarding against an attack flinches the attacker, and counts as an attack for filling trigger.
    //upon releasing sneak, set parry frames for a set number of ticks. Successful parrying creates a shockwave that deals light posture damage, adds 1 second iframe, and adds a trigger charge.
    //upon dodging, set dodge frames for a set number of ticks. Successful dodging slows local time and refills spirit, and counts as an attack for filling trigger.
    //Ticks keep falling under 0, and a configurable number less than 0 serves as the parry cooldown for sneak release.
    //iframes are iframes. Nothing happens on an iframe.
    //priority is iframe>dodge>parry>guard for resolution.

    void resetPosture();

    int getSpirit();

    void setSpirit(int spirit);

    boolean consumeSpirit(int amount);

    int addSpirit(int amount);

    int getMaxSpirit();

    float getMaxPosture();

    float getPosture();

    void setPosture(float posture);

    float addPosture(float amount);

    float consumePosture(LivingEntity assailant, float amount, boolean breach);

    default float consumePosture(LivingEntity assailant, float amount) {
        return consumePosture(assailant, amount, true);
    }

    default float consumePosture(float amount) {
        return consumePosture(null, amount);
    }

    float getRally();

    void setRally(float rally);

    void rally(float quantity);

    void tickProc(String key, double stat);

    default void tickProc(String key) {
        tickProc(key, 1);
    }

    double getProc(String key);

    default boolean alreadyProc(String key) {
        return getProc(key) > 0;
    }

    default boolean isStunned() {
        return getMaxStunTime() > 0;
    }

    int getMaxStunTime();

    int getStunTime();

    void stun(LivingEntity assailant, int time);

    default void stun(int time) {
        stun(null, time);
    }

    boolean isKnockdown();//immune to damage

    void knockdown(LivingEntity assailant, int time);

    default void knockdown(int time) {
        knockdown(null, time);
    }

    Vec3 getMotionConsistently();

    void serverTick();

    void clientTick();

    int getOffhandCooldown();

    void setOffhandCooldown(int cool);

    boolean isDodging();

    boolean canDodge();

    int getDodgeTime();

    void setDodgeTime(int time);

    boolean isParrying();

    boolean canParry();

    int getParryTime();

    void setParryTime(int time);

    boolean isBlocking();

    boolean canBlock();

    int getGuardTime();//special implementation on players

    void setGuardTime(int time);

    boolean isIframe();

    int getIframe();

    void setIframe(int time);

    int getDamageRecordTime();

    float getRecordedDamage();

    void startRecordingDamage(int time);

    void recordDamage(float amount);

    void stopRecording(DamageSource countAs);

    int getPinTime();

    default boolean isPinned() {
        return getPinTime() > 0;
    }

    void pin(int time);

    int getHandBind(InteractionHand hand);

    void setHandBind(InteractionHand hand, int time);

    CompoundTag write();

    void read(CompoundTag from);

    void setOffhandAttack(boolean offhandAttack);

    boolean isOffhandAttack();
}
