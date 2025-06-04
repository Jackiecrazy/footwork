package jackiecrazy.footwork.api;

import jackiecrazy.footwork.move.Move;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface CombatDamageSource {
    float footwork$getCritDamage();

    CombatDamageSource footwork$setCritDamage(float cdmg);

    FootworkDamageArchetype footwork$getDamageTyping();

    CombatDamageSource footwork$setDamageTyping(FootworkDamageArchetype damageTyping);

    boolean footwork$isCrit();

    CombatDamageSource footwork$setCrit(boolean crit);

    ItemStack footwork$getDamageDealer();

    CombatDamageSource footwork$setDamageDealer(ItemStack damageDealer);

    @Nullable
    InteractionHand footwork$getAttackingHand();

    CombatDamageSource footwork$setAttackingHand(InteractionHand attackingHand);

    Entity footwork$getProxy();

    CombatDamageSource footwork$setProxy(Entity proxy);

    Move footwork$getSkillUsed();

    CombatDamageSource footwork$setSkillUsed(Move skillUsed);

    boolean footwork$canProcAutoEffects();

    CombatDamageSource footwork$setProcNormalEffects(boolean procNormalEffects);

    boolean footwork$canProcAttackEffects();

    CombatDamageSource footwork$setProcAttackEffects(boolean procAttackEffects);

    boolean footwork$canProcSkillEffects();

    CombatDamageSource footwork$setProcSkillEffects(boolean procSkillEffects);

    CombatDamageSource footwork$flag(TagKey<DamageType>... tags);

    CombatDamageSource footwork$unflag(TagKey<DamageType>... tags);

    float footwork$getArmorReductionPercentage();

    CombatDamageSource footwork$setArmorReductionPercentage(float armorReductionPercentage);

    float footwork$getKnockbackPercentage();

    CombatDamageSource footwork$setKnockbackPercentage(float perc);

    float footwork$getPostureDamage();

    CombatDamageSource footwork$setPostureDamage(float postureDamage);

    float footwork$getMultiplier();

    CombatDamageSource footwork$setMultiplier(float multiplier);

    boolean footwork$isProjectile();

    CombatDamageSource footwork$setProjectile();

    boolean footwork$isExplosion();

    CombatDamageSource footwork$setExplosion();

    boolean footwork$isBypassArmor();

    boolean footwork$isBypassInvul();

    boolean footwork$isBypassMagic();

    boolean footwork$isBypassEnchantments();

    CombatDamageSource footwork$bypassArmor();

    CombatDamageSource footwork$bypassInvul();

    CombatDamageSource footwork$bypassMagic();

    CombatDamageSource footwork$bypassEnchantments();

    CombatDamageSource footwork$setIsFire();

    CombatDamageSource footwork$setNoAggro();

    boolean footwork$isFire();

    boolean footwork$isNoAggro();

    boolean footwork$isMagic();

    CombatDamageSource footwork$setMagic();

    boolean footwork$isFall();

    CombatDamageSource footwork$setIsFall();

    boolean footwork$isCreativePlayer();

    void footwork$setFinalizedDamage(double amount);
    double footwork$getFinalizedDamage();
}