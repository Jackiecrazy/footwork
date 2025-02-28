package jackiecrazy.footwork.capability.weaponry;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DummyCombatItemCap implements ICombatItemCapability {


    @Override
    public int sweepArea(LivingEntity attacker, ItemStack item) {
        return 0;
    }

    @Override
    public boolean canAttack(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, double orig) {
        return false;
    }

    @Override
    public void attackStart(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, double orig) {

    }

    @Override
    public boolean critCheck(LivingEntity attacker, LivingEntity target, ItemStack item, double crit, boolean vanCrit) {
        return Event.Result.DEFAULT;
    }

    @Override
    public float critDamage(LivingEntity attacker, LivingEntity target, ItemStack item) {
        return 0;
    }

    @Override
    public float damageMultiplier(LivingEntity attacker, LivingEntity target, ItemStack item) {
        return 0;
    }

    @Override
    public float onKnockingBack(LivingEntity attacker, LivingEntity target, ItemStack item, double orig) {
        return 0;
    }

    @Override
    public float onBeingKnockedBack(LivingEntity attacker, LivingEntity target, ItemStack item, double orig) {
        return 0;
    }

    @Override
    public float hurtStart(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, double orig) {
        return 0;
    }

    @Override
    public float damageStart(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, double orig) {
        return 0;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, double orig) {
        return 0;
    }

    @Override
    public float onBeingHurt(DamageSource ds, LivingEntity defender, ItemStack item, double amount) {
        return 0;
    }

    @Override
    public float onBeingDamaged(DamageSource ds, LivingEntity defender, ItemStack item, double amount) {
        return 0;
    }

    @Override
    public boolean canBlock(LivingEntity defender, Entity attacker, ItemStack item, boolean recharged, double amount) {
        return false;
    }

    @Override
    public void onParry(LivingEntity attacker, LivingEntity defender, ItemStack item, double amount) {

    }

    @Override
    public void onOtherHandParry(LivingEntity attacker, LivingEntity defender, ItemStack item, double amount) {

    }

    @Override
    public float postureMultiplierDefend(Entity attacker, LivingEntity defender, ItemStack item, double amount) {
        return 0;
    }

    @Override
    public float postureDealtBase(LivingEntity attacker, LivingEntity defender, ItemStack item, double amount) {
        return 0;
    }
}
