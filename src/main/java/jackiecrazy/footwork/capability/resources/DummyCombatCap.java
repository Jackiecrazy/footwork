package jackiecrazy.footwork.capability.resources;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DummyCombatCap implements ICombatCapability {


    @Override
    public void updateDefenselessStatus() {

    }

    @Override
    public float getMaxMight() {
        return 0;
    }

    @Override
    public float getMight() {
        return 0;
    }

    @Override
    public void setMight(float amount) {

    }

    @Override
    public float addMight(float amount) {
        return 0;
    }

    @Override
    public boolean consumeMight(float amount, float above) {
        return false;
    }

    @Override
    public int getMightGrace() {
        return 0;
    }

    @Override
    public void setMightGrace(int amount) {

    }


    @Override
    public float getMaxSpirit() {
        return 0;
    }

    @Override
    public float getSpirit() {
        return 0;
    }

    @Override
    public void setSpirit(float amount) {

    }

    @Override
    public float addSpirit(float amount) {
        return 0;
    }

    @Override
    public boolean consumeSpirit(float amount, float above) {
        return false;
    }

    @Override
    public int getSpiritGrace() {
        return 0;
    }

    @Override
    public void setSpiritGrace(int amount) {

    }

    @Override
    public float getMaxPosture() {
        return 0;
    }


    @Override
    public float getPosture() {
        return 0;
    }

    @Override
    public void setPosture(float amount) {

    }

    @Override
    public float addPosture(float amount) {
        return 0;
    }

    @Override
    public float consumePosture(LivingEntity assailant, float amount, float above, boolean force) {
        return 0;
    }

    @Override
    public int getPostureGrace() {
        return 0;
    }

    @Override
    public void setPostureGrace(int amount) {

    }


    @Override
    public int getMaxStunTime() {
        return 0;
    }

    @Override
    public int getStunTime() {
        return 0;
    }

    @Override
    public void stun(int time) {

    }

    @Override
    public int getMaxKnockdownTime() {
        return 0;
    }

    @Override
    public int getKnockdownTime() {
        return 0;
    }

    @Override
    public void knockdown(int time) {

    }

    @Override
    public boolean isStunned() {
        return false;
    }

    @Override
    public boolean isKnockedDown() {
        return false;
    }


    @Override
    public int getFractureCount() {
        return 0;
    }

    @Override
    public int getFractureCount(LivingEntity appliedBy) {
        return 0;
    }

    HashMap<UUID, Integer> EMPTY=new HashMap<>();

    @Override
    public HashMap<UUID, Integer> getFractureList() {
        return EMPTY;
    }

    @Override
    public boolean addFracture(@Nullable LivingEntity source, int amount) {
        return true;
    }

    @Override
    public void clearFracture(@Nullable LivingEntity of, boolean clearInvalid) {

    }

    @Override
    public float getMaxFracture() {
        return 0;
    }

    @Override
    public int getMaxExposeTime() {
        return 0;
    }

    @Override
    public int getExposeTime() {
        return 0;
    }

    @Override
    public void expose(int time) {

    }

    @Override
    public boolean isExposed() {
        return false;
    }


    @Override
    public float getRank() {
        return 0;
    }

    @Override
    public void setRank(float amount) {

    }

    @Override
    public void setAdrenalineCooldown(int amount) {

    }

    @Override
    public boolean halvedAdrenaline() {
        return false;
    }

    @Override
    public float addRank(float amount) {
        return 0;
    }

    @Override
    public boolean consumeRank(float amount, float above) {
        return false;
    }

    @Override
    public int getOffhandCooldown() {
        return 0;
    }

    @Override
    public void setOffhandCooldown(int amount) {

    }

    @Override
    public int getRollTime() {
        return 0;
    }

    @Override
    public void setRollTime(int amount) {

    }

    @Override
    public void decrementRollTime(int amount) {

    }

    @Override
    public boolean isOffhandAttack() {
        return false;
    }

    @Override
    public void setOffhandAttack(boolean off) {

    }

    @Override
    public boolean isCombatMode() {
        return false;
    }

    @Override
    public void toggleCombatMode(boolean on) {

    }

    @Override
    public int getHandBind(InteractionHand h) {
        return 0;
    }

    @Override
    public void setHandBind(InteractionHand h, int amount) {

    }

    @Override
    public boolean consumeEvade() {
        return false;
    }

    @Override
    public int getEvade() {
        return 0;
    }

    @Override
    public void setEvade(int value) {

    }

    @Override
    public float getCachedCooldown() {
        return 0;
    }

    @Override
    public void setCachedCooldown(float value) {

    }

    @Override
    public int getForcedSweep() {
        return 0;
    }

    @Override
    public void setForcedSweep(int angle) {

    }

    @Override
    public void clientTick() {

    }

    @Override
    public void serverTick() {

    }

    @Override
    public void sync() {

    }

    @Override
    public ItemStack getTempItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void setTempItemStack(ItemStack is) {

    }

    @Override
    public void read(CompoundTag tag) {

    }

    @Override
    public int getParryingTick() {
        return 0;
    }

    @Override
    public void setParryingTick(int parrying) {

    }

    @Override
    public int getSweepTick() {
        return 0;
    }

    @Override
    public void setSweepTick(int tick) {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public Vec3 getMotionConsistently() {
        return Vec3.ZERO;
    }

    @Override
    public CompoundTag write() {
        return new CompoundTag();
    }

    @Override
    public void addRangedMight(boolean pass) {

    }

    @Override
    public boolean isStaggeringStrike() {
        return false;
    }

    @Override
    public int getRetina() {
        return 0;
    }

    @Override
    public float visionRange() {
        return 0;
    }
}
