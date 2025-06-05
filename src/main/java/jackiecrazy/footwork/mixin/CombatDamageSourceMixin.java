package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.api.FootworkDamageArchetype;
import jackiecrazy.footwork.api.FootworkDamageTypeTags;
import jackiecrazy.footwork.move.Move;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
@Mixin(DamageSource.class)
public abstract class CombatDamageSourceMixin implements CombatDamageSource {
    private static final List<TagKey<DamageType>> PHYSICAL = List.of(DamageTypeTags.BYPASSES_COOLDOWN);
    private static final List<TagKey<DamageType>> MAGICAL = List.of(DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.AVOIDS_GUARDIAN_THORNS);
    private static final List<TagKey<DamageType>> TRUE = List.of(DamageTypeTags.BYPASSES_RESISTANCE, DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_EFFECTS, DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.NO_IMPACT, DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS);
    @Unique
    private final Collection<TagKey<DamageType>> flags = new HashSet<>();
    @Unique
    private double finalDamage;
    @Unique
    private float original = -1;
    @Unique
    private float finalized = 0;
    @Unique
    private ItemStack damageDealer = ItemStack.EMPTY;
    @Unique
    private InteractionHand attackingHand = InteractionHand.MAIN_HAND;
    @Unique
    private Entity proxy;
    @Unique
    private Move skillUsed = null;
    @Unique
    private boolean crit = false;
    @Unique
    private float cdmg = 1.5f;
    @Unique
    private float postureDamage = -1;
    @Unique
    private float armorPierce = 0f, knockback = 1f, multiplier = 1f;
    @Unique
    private FootworkDamageArchetype damageTyping = FootworkDamageArchetype.PHYSICAL;

    @Shadow
    @Nullable
    public abstract Entity getEntity();

    @Shadow
    public abstract boolean is(TagKey<DamageType> damageTypeKey);

    @Override
    public float footwork$getCritDamage() {
        return cdmg;
    }

    @Override
    public CombatDamageSource footwork$setCritDamage(float cdmg) {
        this.cdmg = cdmg;
        return this;
    }

    @Override
    public FootworkDamageArchetype footwork$getDamageTyping() {
        return damageTyping;
    }

    @Override
    public CombatDamageSource footwork$setDamageTyping(FootworkDamageArchetype damageTyping) {
        this.damageTyping = damageTyping;
        return this;
    }

    @Override
    public boolean footwork$isCrit() {
        return crit;
    }

    @Override
    public CombatDamageSource footwork$setCrit(boolean crit) {
        this.crit = crit;
        return this;
    }

    @Override
    public ItemStack footwork$getDamageDealer() {
        return damageDealer;
    }

    @Override
    public CombatDamageSource footwork$setDamageDealer(ItemStack damageDealer) {
        this.damageDealer = damageDealer;
        return this;
    }

    @Nullable
    @Override
    public InteractionHand footwork$getAttackingHand() {
        return attackingHand;
    }

    @Override
    public CombatDamageSource footwork$setAttackingHand(InteractionHand attackingHand) {
        this.attackingHand = attackingHand;
        return this;
    }

    @Override
    public Entity footwork$getProxy() {
        return proxy;
    }

    @Override
    public CombatDamageSource footwork$setProxy(Entity proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Move footwork$getSkillUsed() {
        return skillUsed;
    }

    @Override
    public CombatDamageSource footwork$setSkillUsed(Move skillUsed) {
        this.skillUsed = skillUsed;
        return this;
    }

    @Override
    public boolean footwork$canProcAutoEffects() {
        return flags.contains(FootworkDamageTypeTags.AUTO);
    }

    @Override
    public CombatDamageSource footwork$setProcNormalEffects(boolean procNormalEffects) {
        if (procNormalEffects) flags.add(FootworkDamageTypeTags.AUTO);
        else flags.remove(FootworkDamageTypeTags.AUTO);
        return this;
    }

    @Override
    public boolean footwork$canProcAttackEffects() {
        return flags.contains(FootworkDamageTypeTags.ATTACK);
    }

    @Override
    public CombatDamageSource footwork$setProcAttackEffects(boolean procAttackEffects) {
        if (procAttackEffects) flags.add(FootworkDamageTypeTags.ATTACK);
        else flags.remove(FootworkDamageTypeTags.ATTACK);
        return this;
    }

    @Override
    public boolean footwork$canProcSkillEffects() {
        return flags.contains(FootworkDamageTypeTags.SKILL);
    }

    @Override
    public CombatDamageSource footwork$setProcSkillEffects(boolean procSkillEffects) {
        if (procSkillEffects) flags.add(FootworkDamageTypeTags.SKILL);
        else flags.remove(FootworkDamageTypeTags.SKILL);
        return this;
    }

    @SafeVarargs
    @Override
    public final CombatDamageSource footwork$flag(TagKey<DamageType>... tags) {
        flags.addAll(Arrays.asList(tags));
        return this;
    }

    @SafeVarargs
    @Override
    public final CombatDamageSource footwork$unflag(TagKey<DamageType>... tags) {
        for (TagKey<DamageType> tag : tags)
            flags.remove(tag);
        return this;
    }

    @Override
    public float footwork$getArmorReductionPercentage() {
        return armorPierce;
    }

    @Override
    public CombatDamageSource footwork$setArmorReductionPercentage(float armorReductionPercentage) {
        armorPierce = armorReductionPercentage;
        return this;
    }

    @Override
    public float footwork$getKnockbackPercentage() {
        return knockback;
    }

    @Override
    public CombatDamageSource footwork$setKnockbackPercentage(float perc) {
        knockback = perc;
        return this;
    }

    @Override
    public float footwork$getPostureDamage() {
        return postureDamage;
    }

    @Override
    public CombatDamageSource footwork$setPostureDamage(float postureDamage) {
        this.postureDamage = postureDamage;
        return this;
    }

    @Override
    public float footwork$getMultiplier() {
        return multiplier;
    }

    @Override
    public CombatDamageSource footwork$setMultiplier(float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("RETURN"), cancellable = true)
    public void is(TagKey<DamageType> type, CallbackInfoReturnable<Boolean> cir) {
        switch (damageTyping) {
            case MAGICAL -> {
                if (MAGICAL.contains(type)) cir.setReturnValue(true);
            }
            case PHYSICAL -> {
                if (PHYSICAL.contains(type)) cir.setReturnValue(true);
            }
            case TRUE -> {
                if (TRUE.contains(type)) cir.setReturnValue(true);
            }
        }
        if (flags.contains(type)) cir.setReturnValue(true);
    }


    /**
     * ye olde tags here
     */

    @Override
    public boolean footwork$isProjectile() {
        return is(DamageTypeTags.IS_PROJECTILE);
    }

    @Override
    public CombatDamageSource footwork$setProjectile() {
        footwork$flag(DamageTypeTags.IS_PROJECTILE);
        return this;
    }

    @Override
    public boolean footwork$isExplosion() {
        return is(DamageTypeTags.IS_PROJECTILE);
    }

    @Override
    public CombatDamageSource footwork$setExplosion() {
        footwork$flag(DamageTypeTags.IS_EXPLOSION);
        return this;
    }

    @Override
    public boolean footwork$isBypassArmor() {
        return is(DamageTypeTags.BYPASSES_ARMOR);
    }

    @Override
    public boolean footwork$isBypassInvul() {
        return is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean footwork$isBypassMagic() {
        return is(DamageTypeTags.BYPASSES_RESISTANCE);
    }

    @Override
    public boolean footwork$isBypassEnchantments() {
        return is(DamageTypeTags.BYPASSES_ENCHANTMENTS);
    }

    @Override
    public CombatDamageSource footwork$bypassArmor() {
        footwork$flag(DamageTypeTags.BYPASSES_ARMOR);
        return this;
    }

    @Override
    public CombatDamageSource footwork$bypassInvul() {
        footwork$flag(DamageTypeTags.BYPASSES_INVULNERABILITY);
        return this;
    }

    @Override
    public CombatDamageSource footwork$bypassMagic() {
        footwork$flag(DamageTypeTags.BYPASSES_EFFECTS);
        return this;
    }

    @Override
    public CombatDamageSource footwork$bypassEnchantments() {
        footwork$flag(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        return this;
    }

    @Override
    public CombatDamageSource footwork$setIsFire() {
        footwork$flag(DamageTypeTags.IS_FIRE);
        return this;
    }

    @Override
    public CombatDamageSource footwork$setNoAggro() {
        footwork$flag(DamageTypeTags.NO_ANGER);
        return this;
    }

    @Override
    public boolean footwork$isFire() {
        return is(DamageTypeTags.IS_FIRE);
    }

    @Override
    public boolean footwork$isNoAggro() {
        return is(DamageTypeTags.NO_ANGER);
    }

    @Override
    public boolean footwork$isMagic() {
        return is(DamageTypeTags.WITCH_RESISTANT_TO);
    }

    @Override
    public CombatDamageSource footwork$setMagic() {
        footwork$flag(DamageTypeTags.WITCH_RESISTANT_TO);
        return this;
    }

    @Override
    public boolean footwork$isFall() {
        return is(DamageTypeTags.IS_FALL);
    }

    @Override
    public CombatDamageSource footwork$setIsFall() {
        footwork$flag(DamageTypeTags.IS_FALL);
        return this;
    }

    @Override
    public boolean footwork$isCreativePlayer() {
        Entity entity = getEntity();
        return entity instanceof Player && ((Player) entity).getAbilities().instabuild;
    }

    @Override
    public double footwork$getFinalizedDamage() {
        return finalDamage;
    }

    @Override
    public void footwork$setFinalizedDamage(double amount) {
        finalDamage = amount;
    }
}