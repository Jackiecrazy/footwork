package jackiecrazy.footwork.api;

import jackiecrazy.footwork.move.Move;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CombatDamageSource extends DamageSource {
    private static final List<TagKey<DamageType>> PHYSICAL = List.of(DamageTypeTags.BYPASSES_COOLDOWN);
    private static final List<TagKey<DamageType>> MAGICAL = List.of(DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.AVOIDS_GUARDIAN_THORNS);
    private static final List<TagKey<DamageType>> TRUE = List.of(DamageTypeTags.BYPASSES_RESISTANCE, DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_EFFECTS, DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.NO_IMPACT, DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS);
    private float original = 0;
    private ItemStack damageDealer = ItemStack.EMPTY;
    private Collection<TagKey<DamageType>> flags = new HashSet<>();
    private InteractionHand attackingHand = InteractionHand.MAIN_HAND;
    private Entity proxy = null;
    private Move skillUsed = null;
    private boolean crit = false;
    private float cdmg = 1.5f;
    private float postureDamage = -1;
    private float armor = 1f, knockback = 1f, multiplier = 1f;
    private TYPE damageTyping = TYPE.PHYSICAL;

    public CombatDamageSource(@Nonnull Entity entity) {
        this(entity, null, entity.position());
    }
    public CombatDamageSource(@Nonnull Entity entity, @Nullable Entity proxy) {
        this(entity, proxy, entity.position());
    }
    public CombatDamageSource(@Nonnull Entity entity, @Nullable Vec3 pos) {
        this(entity, null, pos);
    }
    public CombatDamageSource(@Nonnull Entity entity, @Nullable Entity proxy, @Nullable Vec3 pos) {
        super(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(entity instanceof Player ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK), proxy, entity, pos);
    }

    public static CombatDamageSource causeSelfDamage(LivingEntity to) {
        return new CombatDamageSource(to);
    }

    public float getCritDamage() {
        return cdmg;
    }

    public CombatDamageSource setCritDamage(float cdmg) {
        this.cdmg = cdmg;
        return this;
    }

    public TYPE getDamageTyping() {
        return damageTyping;
    }

    public CombatDamageSource setDamageTyping(TYPE damageTyping) {
        this.damageTyping = damageTyping;
        return this;
    }

    public boolean isCrit() {
        return crit;
    }

    public CombatDamageSource setCrit(boolean crit) {
        this.crit = crit;
        return this;
    }

    public ItemStack getDamageDealer() {
        return damageDealer;
    }

    public CombatDamageSource setDamageDealer(ItemStack damageDealer) {
        this.damageDealer = damageDealer;
        return this;
    }

    @Nullable
    public InteractionHand getAttackingHand() {
        return attackingHand;
    }

    public CombatDamageSource setAttackingHand(InteractionHand attackingHand) {
        this.attackingHand = attackingHand;
        return this;
    }

    public Entity getProxy() {
        return proxy;
    }

    public CombatDamageSource setProxy(Entity proxy) {
        this.proxy = proxy;
        return this;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Entity getDirectEntity() {
        return proxy;
    }

    @Override
    public boolean scalesWithDifficulty() {
        return super.scalesWithDifficulty();
    }

    @Override
    public boolean isIndirect() {
        return proxy!=null;
    }

    public Move getSkillUsed() {
        return skillUsed;
    }

    public CombatDamageSource setSkillUsed(Move skillUsed) {
        this.skillUsed = skillUsed;
        return this;
    }

    public boolean canProcAutoEffects() {
        return flags.contains(FootworkDamageTypeTags.AUTO);
    }

    public CombatDamageSource setProcNormalEffects(boolean procNormalEffects) {
        if (procNormalEffects)
            flags.add(FootworkDamageTypeTags.AUTO);
        else flags.remove(FootworkDamageTypeTags.AUTO);
        return this;
    }

    public boolean canProcAttackEffects() {
        return flags.contains(FootworkDamageTypeTags.ATTACK);
    }

    public CombatDamageSource setProcAttackEffects(boolean procAttackEffects) {
        if (procAttackEffects)
            flags.add(FootworkDamageTypeTags.ATTACK);
        else flags.remove(FootworkDamageTypeTags.ATTACK);
        return this;
    }

    public boolean canProcSkillEffects() {
        return flags.contains(FootworkDamageTypeTags.SKILL);
    }

    public CombatDamageSource setProcSkillEffects(boolean procSkillEffects) {
        if (procSkillEffects)
            flags.add(FootworkDamageTypeTags.SKILL);
        else flags.remove(FootworkDamageTypeTags.SKILL);
        return this;
    }

    public CombatDamageSource flag(TagKey<DamageType> tag) {
        flags.add(tag);
        return this;
    }

    public CombatDamageSource unflag(TagKey<DamageType> tag) {
        flags.remove(tag);
        return this;
    }

    public float getArmorReductionPercentage() {
        return armor;
    }

    public CombatDamageSource setArmorReductionPercentage(float armorReductionPercentage) {
        armor = armorReductionPercentage;
        return this;
    }

    public float getKnockbackPercentage() {
        return knockback;
    }

    public CombatDamageSource setKnockbackPercentage(float perc) {
        knockback = perc;
        return this;
    }

    public float getPostureDamage() {
        return postureDamage;
    }

    public CombatDamageSource setPostureDamage(float postureDamage) {
        this.postureDamage = postureDamage;
        return this;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public CombatDamageSource setMultiplier(float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    public float getOriginalDamage() {
        return original;
    }

    public CombatDamageSource setOriginalDamage(float original) {
        this.original = original;
        return this;
    }
    float absorption;

    public float getDockedAbsorption() {
        return absorption;
    }

    public CombatDamageSource setDockedAbsorption(float absorption) {
        this.absorption=absorption;
        original-=Math.min(absorption, original);
        return this;
    }

    @Override
    public boolean is(@NotNull TagKey<DamageType> type) {
        switch (damageTyping) {
            case MAGICAL -> {
                if (MAGICAL.contains(type)) return true;
            }
            case PHYSICAL -> {
                if (PHYSICAL.contains(type)) return true;
            }
            case TRUE -> {
                if (TRUE.contains(type)) return true;
            }
        }
        if (flags.contains(type)) return true;
        return super.is(type);
    }

    public enum TYPE {
        PHYSICAL,//reduced by absorption, deflection, shatter, and armor
        MAGICAL,//reduced by resist
        TRUE//not reduced by anything
    }

    /**
     * ye olde tags here
     */

    public boolean isProjectile() {
        return is(DamageTypeTags.IS_PROJECTILE);
    }

    public DamageSource setProjectile() {
        flag(DamageTypeTags.IS_PROJECTILE);
        return this;
    }

    public boolean isExplosion() {
        return is(DamageTypeTags.IS_PROJECTILE);
    }

    public DamageSource setExplosion() {
        flag(DamageTypeTags.IS_EXPLOSION);
        return this;
    }

    public boolean isBypassArmor() {
        return is(DamageTypeTags.BYPASSES_ARMOR);
    }

    public boolean isBypassInvul() {
        return is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    public boolean isBypassMagic() {
        return is(DamageTypeTags.BYPASSES_RESISTANCE);
    }

    public boolean isBypassEnchantments() {
        return is(DamageTypeTags.BYPASSES_ENCHANTMENTS);
    }

    public DamageSource bypassArmor() {
        flag(DamageTypeTags.BYPASSES_ARMOR);
        return this;
    }

    public DamageSource bypassInvul() {
        flag(DamageTypeTags.BYPASSES_INVULNERABILITY);
        return this;
    }

    public DamageSource bypassMagic() {
        flag(DamageTypeTags.BYPASSES_EFFECTS);
        return this;
    }

    public DamageSource bypassEnchantments() {
        flag(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        return this;
    }

    public DamageSource setIsFire() {
        flag(DamageTypeTags.IS_FIRE);
        return this;
    }

    public DamageSource setNoAggro() {
        flag(DamageTypeTags.NO_ANGER);
        return this;
    }

    public boolean isFire() {
        return is(DamageTypeTags.IS_FIRE);
    }

    public boolean isNoAggro() {
        return is(DamageTypeTags.NO_ANGER);
    }

    public boolean isMagic() {
        return is(DamageTypeTags.WITCH_RESISTANT_TO);
    }

    public DamageSource setMagic() {
        flag(DamageTypeTags.WITCH_RESISTANT_TO);
        return this;
    }

    public boolean isFall() {
        return is(DamageTypeTags.IS_FALL);
    }

    public DamageSource setIsFall() {
        flag(DamageTypeTags.IS_FALL);
        return this;
    }

    public boolean isCreativePlayer() {
        Entity entity = this.getEntity();
        return entity instanceof Player && ((Player)entity).getAbilities().instabuild;
    }
}
