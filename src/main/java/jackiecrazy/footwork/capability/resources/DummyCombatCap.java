package jackiecrazy.footwork.capability.resources;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class DummyCombatCap implements ICombatCapability {

    @Override
    public void updateDefenselessStatus() {

    }

    @Override
    public int getSpirit() {
        return 0;
    }

    @Override
    public void setSpirit(float spirit) {

    }

    @Override
    public boolean consumeSpirit(int amount) {
        return false;
    }

    @Override
    public int addSpirit(int amount) {
        return 0;
    }

    @Override
    public int getMaxSpirit() {
        return 0;
    }

    @Override
    public float getPosture() {
        return 0;
    }

    @Override
    public void setPosture(float posture) {

    }

    @Override
    public float addPosture(float amount) {
        return 0;
    }

    @Override
    public float consumePosture(LivingEntity assailant, float amount, boolean breach) {
        return 0;
    }

    @Override
    public float getRally() {
        return 0;
    }

    @Override
    public void setRally(float rally) {

    }

    @Override
    public float addRally(float amount) {
        return 0;
    }

    @Override
    public void convertRally(float quantity) {

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
    public void stun(LivingEntity assailant, int time) {

    }

    @Override
    public boolean isKnockdown() {
        return false;
    }

    @Override
    public void knockdown(LivingEntity assailant, int time) {

    }

    @Override
    public Vec3 getMotionConsistently() {
        return null;
    }

    @Override
    public void serverTick() {

    }

    @Override
    public void clientTick() {

    }

    @Override
    public int getOffhandCooldown() {
        return 0;
    }

    @Override
    public void setOffhandCooldown(int cool) {

    }

    @Override
    public boolean isDodging() {
        return false;
    }

    @Override
    public boolean canDodge() {
        return false;
    }

    @Override
    public int getDodgeTime() {
        return 0;
    }

    @Override
    public void setDodgeTime(int time) {

    }

    @Override
    public boolean isParrying() {
        return false;
    }

    @Override
    public boolean canParry() {
        return false;
    }

    @Override
    public int getParryTime() {
        return 0;
    }

    @Override
    public void setParryTime(int time) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean canBlock() {
        return false;
    }

    @Override
    public int getGuardTime() {
        return 0;
    }

    @Override
    public void setGuardTime(int time) {

    }

    @Override
    public boolean isIframe() {
        return false;
    }

    @Override
    public int getIframe() {
        return 0;
    }

    @Override
    public void setIframe(int time) {

    }

    @Override
    public int getDamageRecordTime() {
        return 0;
    }

    @Override
    public float getRecordedDamage() {
        return 0;
    }

    @Override
    public void recordDamage(LivingEntity recorder, float amount) {

    }

    @Override
    public void stopRecording(DamageSource countAs) {

    }

    @Override
    public int getPinTime() {
        return 0;
    }

    @Override
    public void pin(int time) {

    }

    @Override
    public int getHandBind(InteractionHand hand) {
        return 0;
    }

    @Override
    public void setHandBind(InteractionHand hand, int time) {

    }

    @Override
    public CompoundTag write() {
        return null;
    }

    @Override
    public void read(CompoundTag from) {

    }

    @Override
    public boolean isOffhandAttack() {
        return false;
    }
}
