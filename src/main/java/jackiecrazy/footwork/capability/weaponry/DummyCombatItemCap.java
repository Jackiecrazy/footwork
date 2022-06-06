package jackiecrazy.footwork.capability.weaponry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class DummyCombatItemCap implements ICombatItemCapability {


    @Override
    public int sweepArea(LivingEntity attacker, ItemStack item) {
        return 0;
    }

    @Override
    public boolean canAttack(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, float orig) {
        return false;
    }

    @Override
    public void attackStart(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, float orig) {

    }

    @Override
    public Event.Result critCheck(LivingEntity attacker, LivingEntity target, ItemStack item, float crit, boolean vanCrit) {
        return null;
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
    public float onKnockingBack(LivingEntity attacker, LivingEntity target, ItemStack item, float orig) {
        return 0;
    }

    @Override
    public float onBeingKnockedBack(LivingEntity attacker, LivingEntity target, ItemStack item, float orig) {
        return 0;
    }

    @Override
    public float hurtStart(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, float orig) {
        return 0;
    }

    @Override
    public float damageStart(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, float orig) {
        return 0;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, LivingEntity attacker, LivingEntity target, ItemStack item, float orig) {
        return 0;
    }

    @Override
    public float onBeingHurt(DamageSource ds, LivingEntity defender, ItemStack item, float amount) {
        return 0;
    }

    @Override
    public float onBeingDamaged(DamageSource ds, LivingEntity defender, ItemStack item, float amount) {
        return 0;
    }

    @Override
    public boolean canBlock(LivingEntity defender, Entity attacker, ItemStack item, boolean recharged, float amount) {
        return false;
    }

    @Override
    public void onParry(LivingEntity attacker, LivingEntity defender, ItemStack item, float amount) {

    }

    @Override
    public void onOtherHandParry(LivingEntity attacker, LivingEntity defender, ItemStack item, float amount) {

    }

    @Override
    public float postureMultiplierDefend(Entity attacker, LivingEntity defender, ItemStack item, float amount) {
        return 0;
    }

    @Override
    public float postureDealtBase(LivingEntity attacker, LivingEntity defender, ItemStack item, float amount) {
        return 0;
    }

    public static class Storage implements Capability.IStorage<ICombatItemCapability> {

        @Nullable
        @Override
        public Tag writeNBT(Capability<ICombatItemCapability> capability, ICombatItemCapability instance, Direction side) {
            return new CompoundTag();
        }

        @Override
        public void readNBT(Capability<ICombatItemCapability> capability, ICombatItemCapability instance, Direction side, Tag nbt) {

        }
    }
}
