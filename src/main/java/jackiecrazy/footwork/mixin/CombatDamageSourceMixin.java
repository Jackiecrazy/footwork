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
    @Shadow
    @Nullable
    public abstract Entity getEntity();

    @Shadow
    public abstract boolean is(TagKey<DamageType> damageTypeKey);

    private static final List<TagKey<DamageType>> PHYSICAL = List.of(DamageTypeTags.BYPASSES_COOLDOWN);
    private static final List<TagKey<DamageType>> MAGICAL = List.of(DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.AVOIDS_GUARDIAN_THORNS);
    private static final List<TagKey<DamageType>> TRUE = List.of(DamageTypeTags.BYPASSES_RESISTANCE, DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_EFFECTS, DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.NO_IMPACT, DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS);
    private float original = -1;
    private float finalized = 0;
    private ItemStack damageDealer = ItemStack.EMPTY;
    private final Collection<TagKey<DamageType>> flags = new HashSet<>();
    private InteractionHand attackingHand = InteractionHand.MAIN_HAND;
    private Entity proxy;
    private Move skillUsed = null;
    private boolean crit = false;
    private float cdmg = 1.5f;
    private float postureDamage = -1;
    private float armorPierce = 0f, knockback = 1f, multiplier = 1f;
    private FootworkDamageArchetype damageTyping = FootworkDamageArchetype.PHYSICAL;

    @Override
    public float getCritDamage() {
        return cdmg;
    }

    @Override
    public CombatDamageSourceMixin setCritDamage(float cdmg) {
        this.cdmg = cdmg;
        return this;
    }

    @Override
    public FootworkDamageArchetype getDamageTyping() {
        return damageTyping;
    }

    @Override
    public CombatDamageSourceMixin setDamageTyping(FootworkDamageArchetype damageTyping) {
        this.damageTyping = damageTyping;
        return this;
    }

    @Override
    public boolean isCrit() {
        return crit;
    }

    @Override
    public CombatDamageSourceMixin setCrit(boolean crit) {
        this.crit = crit;
        return this;
    }

    @Override
    public ItemStack getDamageDealer() {
        return damageDealer;
    }

    @Override
    public CombatDamageSourceMixin setDamageDealer(ItemStack damageDealer) {
        this.damageDealer = damageDealer;
        return this;
    }

    @Nullable
    @Override
    public InteractionHand getAttackingHand() {
        return attackingHand;
    }

    @Override
    public CombatDamageSourceMixin setAttackingHand(InteractionHand attackingHand) {
        this.attackingHand = attackingHand;
        return this;
    }

    @Override
    public Entity getProxy() {
        return proxy;
    }

    @Override
    public CombatDamageSourceMixin setProxy(Entity proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Move getSkillUsed() {
        return skillUsed;
    }

    @Override
    public CombatDamageSourceMixin setSkillUsed(Move skillUsed) {
        this.skillUsed = skillUsed;
        return this;
    }

    @Override
    public boolean canProcAutoEffects() {
        return flags.contains(FootworkDamageTypeTags.AUTO);
    }

    @Override
    public CombatDamageSourceMixin setProcNormalEffects(boolean procNormalEffects) {
        if (procNormalEffects)
            flags.add(FootworkDamageTypeTags.AUTO);
        else flags.remove(FootworkDamageTypeTags.AUTO);
        return this;
    }

    @Override
    public boolean canProcAttackEffects() {
        return flags.contains(FootworkDamageTypeTags.ATTACK);
    }

    @Override
    public CombatDamageSourceMixin setProcAttackEffects(boolean procAttackEffects) {
        if (procAttackEffects)
            flags.add(FootworkDamageTypeTags.ATTACK);
        else flags.remove(FootworkDamageTypeTags.ATTACK);
        return this;
    }

    @Override
    public boolean canProcSkillEffects() {
        return flags.contains(FootworkDamageTypeTags.SKILL);
    }

    @Override
    public CombatDamageSourceMixin setProcSkillEffects(boolean procSkillEffects) {
        if (procSkillEffects)
            flags.add(FootworkDamageTypeTags.SKILL);
        else flags.remove(FootworkDamageTypeTags.SKILL);
        return this;
    }

    @Override
    public CombatDamageSourceMixin flag(TagKey<DamageType>... tags) {
        flags.addAll(Arrays.asList(tags));
        return this;
    }

    @Override
    public CombatDamageSourceMixin unflag(TagKey<DamageType>... tags) {
        for (TagKey<DamageType> tag : tags)
            flags.remove(tag);
        return this;
    }

    @Override
    public float getArmorReductionPercentage() {
        return armorPierce;
    }

    @Override
    public CombatDamageSourceMixin setArmorReductionPercentage(float armorReductionPercentage) {
        armorPierce = armorReductionPercentage;
        return this;
    }

    @Override
    public float getKnockbackPercentage() {
        return knockback;
    }

    @Override
    public CombatDamageSourceMixin setKnockbackPercentage(float perc) {
        knockback = perc;
        return this;
    }

    @Override
    public float getPostureDamage() {
        return postureDamage;
    }

    @Override
    public CombatDamageSourceMixin setPostureDamage(float postureDamage) {
        this.postureDamage = postureDamage;
        return this;
    }

    @Override
    public float getMultiplier() {
        return multiplier;
    }

    @Override
    public CombatDamageSourceMixin setMultiplier(float multiplier) {
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
    public boolean isProjectile() {
        return is(DamageTypeTags.IS_PROJECTILE);
    }

    @Override
    public CombatDamageSourceMixin setProjectile() {
        flag(DamageTypeTags.IS_PROJECTILE);
        return this;
    }

    @Override
    public boolean isExplosion() {
        return is(DamageTypeTags.IS_PROJECTILE);
    }

    @Override
    public CombatDamageSourceMixin setExplosion() {
        flag(DamageTypeTags.IS_EXPLOSION);
        return this;
    }

    @Override
    public boolean isBypassArmor() {
        return is(DamageTypeTags.BYPASSES_ARMOR);
    }

    @Override
    public boolean isBypassInvul() {
        return is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean isBypassMagic() {
        return is(DamageTypeTags.BYPASSES_RESISTANCE);
    }

    @Override
    public boolean isBypassEnchantments() {
        return is(DamageTypeTags.BYPASSES_ENCHANTMENTS);
    }

    @Override
    public CombatDamageSourceMixin bypassArmor() {
        flag(DamageTypeTags.BYPASSES_ARMOR);
        return this;
    }

    @Override
    public CombatDamageSourceMixin bypassInvul() {
        flag(DamageTypeTags.BYPASSES_INVULNERABILITY);
        return this;
    }

    @Override
    public CombatDamageSourceMixin bypassMagic() {
        flag(DamageTypeTags.BYPASSES_EFFECTS);
        return this;
    }

    @Override
    public CombatDamageSourceMixin bypassEnchantments() {
        flag(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        return this;
    }

    @Override
    public CombatDamageSourceMixin setIsFire() {
        flag(DamageTypeTags.IS_FIRE);
        return this;
    }

    @Override
    public CombatDamageSourceMixin setNoAggro() {
        flag(DamageTypeTags.NO_ANGER);
        return this;
    }

    @Override
    public boolean isFire() {
        return is(DamageTypeTags.IS_FIRE);
    }

    @Override
    public boolean isNoAggro() {
        return is(DamageTypeTags.NO_ANGER);
    }

    @Override
    public boolean isMagic() {
        return is(DamageTypeTags.WITCH_RESISTANT_TO);
    }

    @Override
    public CombatDamageSourceMixin setMagic() {
        flag(DamageTypeTags.WITCH_RESISTANT_TO);
        return this;
    }

    @Override
    public boolean isFall() {
        return is(DamageTypeTags.IS_FALL);
    }

    @Override
    public CombatDamageSourceMixin setIsFall() {
        flag(DamageTypeTags.IS_FALL);
        return this;
    }

    @Override
    public boolean isCreativePlayer() {
        Entity entity = getEntity();
        return entity instanceof Player && ((Player) entity).getAbilities().instabuild;
    }
}
