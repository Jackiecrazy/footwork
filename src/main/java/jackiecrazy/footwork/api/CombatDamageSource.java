package jackiecrazy.footwork.api;

import jackiecrazy.footwork.move.Move;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface CombatDamageSource {
    float getCritDamage();

    CombatDamageSource setCritDamage(float cdmg);

    FootworkDamageArchetype getDamageTyping();

    CombatDamageSource setDamageTyping(FootworkDamageArchetype damageTyping);

    boolean isCrit();

    CombatDamageSource setCrit(boolean crit);

    ItemStack getDamageDealer();

    CombatDamageSource setDamageDealer(ItemStack damageDealer);

    @Nullable
    InteractionHand getAttackingHand();

    CombatDamageSource setAttackingHand(InteractionHand attackingHand);

    Entity getProxy();

    CombatDamageSource setProxy(Entity proxy);

    Move getSkillUsed();

    CombatDamageSource setSkillUsed(Move skillUsed);

    boolean canProcAutoEffects();

    CombatDamageSource setProcNormalEffects(boolean procNormalEffects);

    boolean canProcAttackEffects();

    CombatDamageSource setProcAttackEffects(boolean procAttackEffects);

    boolean canProcSkillEffects();

    CombatDamageSource setProcSkillEffects(boolean procSkillEffects);

    CombatDamageSource flag(TagKey<DamageType>... tags);

    CombatDamageSource unflag(TagKey<DamageType>... tags);

    float getArmorReductionPercentage();

    CombatDamageSource setArmorReductionPercentage(float armorReductionPercentage);

    float getKnockbackPercentage();

    CombatDamageSource setKnockbackPercentage(float perc);

    float getPostureDamage();

    CombatDamageSource setPostureDamage(float postureDamage);

    float getMultiplier();

    CombatDamageSource setMultiplier(float multiplier);

    float getOriginalDamage();

    CombatDamageSource setOriginalDamage(float original);

    void setFinalDamage(float fin);

    float getFinalDamage();

    float getDockedAbsorption();

    CombatDamageSource setDockedAbsorption(float absorption);

    boolean isProjectile();

    CombatDamageSource setProjectile();

    boolean isExplosion();

    CombatDamageSource setExplosion();

    boolean isBypassArmor();

    boolean isBypassInvul();

    boolean isBypassMagic();

    boolean isBypassEnchantments();

    CombatDamageSource bypassArmor();

    CombatDamageSource bypassInvul();

    CombatDamageSource bypassMagic();

    CombatDamageSource bypassEnchantments();

    CombatDamageSource setIsFire();

    CombatDamageSource setNoAggro();

    boolean isFire();

    boolean isNoAggro();

    boolean isMagic();

    CombatDamageSource setMagic();

    boolean isFall();

    CombatDamageSource setIsFall();

    boolean isCreativePlayer();
}
