package jackiecrazy.footwork.move.argument;

import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.api.FootworkDamageArchetype;
import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.stack.EquippedItemArgument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.condition.TrueCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DamageArgument implements Argument<DamageSource> {
    private Argument<Entity> source = new CasterEntityArgument();
    private Argument<Entity> proxy = new CasterEntityArgument();
    private FootworkDamageArchetype typing = FootworkDamageArchetype.PHYSICAL;
    private Argument<ItemStack> equip = new EquippedItemArgument();
    private Argument<Double> crit_damage = new FixedNumberArgument(1.5);
    private Argument<Double> posture_damage = new FixedNumberArgument(-1);
    private Argument<Double> armor_pierce_percentage = new FixedNumberArgument(0);
    private Argument<Double> knockback_percentage = new FixedNumberArgument(1);
    private Argument<Double> damage_multiplier = new FixedNumberArgument(1);
    private Condition crit = new FalseCondition();
    private Condition proc_normal = new TrueCondition();
    private Condition proc_attack = new TrueCondition();
    private Condition proc_skill = new FalseCondition();
    private List<ResourceLocation> tags = new ArrayList<>();
    transient Set<TagKey<DamageType>> dtags;

    public DamageSource resolve(MovesetWrapper wrapper, Action parent, @Nullable Entity caster, Entity target) {
        if (dtags == null) {
            dtags = tags.stream().map(a -> TagKey.create(Registries.DAMAGE_TYPE, a)).collect(Collectors.toSet());
        }
        Entity entity = source.resolve(wrapper, parent, caster, target);
        DamageSource ret = new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(entity instanceof Player ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK));
        CombatDamageSource casted = (CombatDamageSource)ret;
        casted
                .footwork$setDamageDealer(equip.resolve(wrapper, parent, caster, target))
                .footwork$setProxy(proxy.resolve(wrapper, parent, caster, target))
                .footwork$setDamageTyping(typing)
                .footwork$setProcAttackEffects(proc_attack.resolve(wrapper, parent, caster, target))
                .footwork$setProcSkillEffects(proc_skill.resolve(wrapper, parent, caster, target))
                .footwork$setProcNormalEffects(proc_normal.resolve(wrapper, parent, caster, target))
                .footwork$setCrit(crit.resolve(wrapper, parent, caster, target))
                .footwork$setCritDamage(crit_damage.resolve(wrapper, parent, caster, target).floatValue())
                .footwork$setArmorReductionPercentage(armor_pierce_percentage.resolve(wrapper, parent, caster, target).floatValue())
                .footwork$setKnockbackPercentage(knockback_percentage.resolve(wrapper, parent, caster, target).floatValue())
                .footwork$setMultiplier(damage_multiplier.resolve(wrapper, parent, caster, target).floatValue())
                .footwork$setPostureDamage(posture_damage.resolve(wrapper, parent, caster, target).floatValue());
        dtags.forEach(casted::footwork$flag);
        return ret;
    }
}
