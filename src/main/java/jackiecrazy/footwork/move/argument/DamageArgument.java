package jackiecrazy.footwork.move.argument;

import jackiecrazy.footwork.api.CombatDamageSource;
import jackiecrazy.footwork.api.FootworkDamageArchetype;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.stack.EquippedItemArgument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.condition.TrueCondition;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

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
    private Condition breach = new TrueCondition();
    private Condition proc_normal = new TrueCondition();
    private Condition proc_attack = new TrueCondition();
    private Condition proc_skill = new FalseCondition();
    private List<ResourceLocation> tags = new ArrayList<>();
    transient Set<TagKey<DamageType>> dtags;

    public DamageSource resolve(ArgumentContext context) {
        if (dtags == null) {
            dtags = tags.stream().map(a -> TagKey.create(Registries.DAMAGE_TYPE, a)).collect(Collectors.toSet());
        }
        Entity entity = source.resolve(context);
        CombatDamageSource ret = new CombatDamageSource(entity);//DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(entity instanceof Player ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK));
        ret
                .setDamageDealer(equip.resolve(context))
                .setProxy(proxy.resolve(context))
                .flagBreach(breach.resolve(context))
                .setDamageTyping(typing)
                .setProcAttackEffects(proc_attack.resolve(context))
                .setProcSkillEffects(proc_skill.resolve(context))
                .setProcNormalEffects(proc_normal.resolve(context))
                .setCrit(crit.resolve(context))
                .setCritDamage(crit_damage.resolve(context).floatValue())
                .setArmorReductionPercentage(armor_pierce_percentage.resolve(context).floatValue())
                .setKnockbackPercentage(knockback_percentage.resolve(context).floatValue())
                .setMultiplier(damage_multiplier.resolve(context).floatValue())
                .setPostureDamage(posture_damage.resolve(context).floatValue());
        dtags.forEach(ret::flag);
        return ret;
    }
}
