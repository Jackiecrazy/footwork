package jackiecrazy.footwork.api;

import jackiecrazy.footwork.move.Move;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ICombatDamageSourceMixin {
    float getCritDamage();

    ICombatDamageSourceMixin setCritDamage(float cdmg);

    FootworkDamageArchetype getDamageTyping();

    ICombatDamageSourceMixin setDamageTyping(FootworkDamageArchetype damageTyping);

    boolean isCrit();

    ICombatDamageSourceMixin setCrit(boolean crit);

    ItemStack getDamageDealer();

    ICombatDamageSourceMixin setDamageDealer(ItemStack damageDealer);

    @Nullable
    InteractionHand getAttackingHand();

    ICombatDamageSourceMixin setAttackingHand(InteractionHand attackingHand);

    Entity getProxy();

    ICombatDamageSourceMixin setProxy(Entity proxy);

    Move getSkillUsed();

    ICombatDamageSourceMixin setSkillUsed(Move skillUsed);

    boolean canProcAutoEffects();

    ICombatDamageSourceMixin setProcNormalEffects(boolean procNormalEffects);

    boolean canProcAttackEffects();

    ICombatDamageSourceMixin setProcAttackEffects(boolean procAttackEffects);

    boolean canProcSkillEffects();

    ICombatDamageSourceMixin setProcSkillEffects(boolean procSkillEffects);

    ICombatDamageSourceMixin flag(TagKey<DamageType>... tags);

    ICombatDamageSourceMixin unflag(TagKey<DamageType>... tags);

    float getArmorReductionPercentage();

    ICombatDamageSourceMixin setArmorReductionPercentage(float armorReductionPercentage);

    float getKnockbackPercentage();

    ICombatDamageSourceMixin setKnockbackPercentage(float perc);

    float getPostureDamage();

    ICombatDamageSourceMixin setPostureDamage(float postureDamage);

    float getMultiplier();

    ICombatDamageSourceMixin setMultiplier(float multiplier);

    boolean isProjectile();

    ICombatDamageSourceMixin setProjectile();

    boolean isExplosion();

    ICombatDamageSourceMixin setExplosion();

    boolean isBypassArmor();

    boolean isBypassInvul();

    boolean isBypassMagic();

    boolean isBypassEnchantments();

    ICombatDamageSourceMixin bypassArmor();

    ICombatDamageSourceMixin bypassInvul();

    ICombatDamageSourceMixin bypassMagic();

    ICombatDamageSourceMixin bypassEnchantments();

    ICombatDamageSourceMixin setIsFire();

    ICombatDamageSourceMixin setNoAggro();

    boolean isFire();

    boolean isNoAggro();

    boolean isMagic();

    ICombatDamageSourceMixin setMagic();

    boolean isFall();

    ICombatDamageSourceMixin setIsFall();

    boolean isCreativePlayer();

    void setFinalizedDamage(double amount);
    double getFinalizedDamage();
}