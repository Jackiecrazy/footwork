package jackiecrazy.footwork.potion;

import jackiecrazy.footwork.api.FootworkAttributes;
import jackiecrazy.footwork.Footwork;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FootworkEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Footwork.MODID);

    //slows posture regeneration by 0.2 per level
    public static final Holder<MobEffect> EXHAUSTION = EFFECTS.register("exhaustion", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xa9a9a9));
    //next attack against a distracted creature applies the distracted bonus and removes distraction
    public static final Holder<MobEffect> DISTRACTION = EFFECTS.register("distraction", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xc98fff));
    //identical to distraction, but not removed on attack
    public static final Holder<MobEffect> CONFUSION = EFFECTS.register("confusion", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x7833b0));
    //identical to distraction, the target will attempt to run away, invoke with EffectUtils.causeFear() for a specific target, or invoke terror and let it handle the rest.
    public static final Holder<MobEffect> FEAR = EFFECTS.register("fear", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xfcfc00));
    //use this to quickly give fear to a target entity.
    public static final Holder<MobEffect> TERROR = EFFECTS.register("terror", TerrorEffect::new);
    //attacks against paralyzed targets apply the unaware bonus, paralyzed targets cannot move
    public static final Holder<MobEffect> PARALYSIS = EFFECTS.register("paralysis", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x2c2c2c).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "stop_moving"), -10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    //identical to paralysis, but adds 4 armor
    public static final Holder<MobEffect> PETRIFY = EFFECTS.register("petrify", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x949494).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "stop_moving"), -10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "stop_moving"), 100, AttributeModifier.Operation.ADD_VALUE).addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "petrified"), 4, AttributeModifier.Operation.ADD_VALUE));
    //identical to paralysis, but is reduced by 1 second for every point of incoming damage
    public static final Holder<MobEffect> SLEEP = EFFECTS.register("sleep", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xc0c0d3e).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "stop_moving"), -10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    //increases incoming non-combat damage by potency
    public static final Holder<MobEffect> VULNERABLE = EFFECTS.register("vulnerable", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x73a915));
    //reduces armor by 2 per level
    public static final Holder<MobEffect> CORROSION = EFFECTS.register("corrosion", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xcfd102).addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "armor_debuff"), -2, AttributeModifier.Operation.ADD_VALUE));
    //increases armor by 2 per level
    public static final Holder<MobEffect> FORTIFICATION = EFFECTS.register("fortification", () -> new FootworkEffect(MobEffectCategory.BENEFICIAL, 0x0eb00c).addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "armor_buff"), 2, AttributeModifier.Operation.ADD_VALUE));
    //increases posture damage by 20% per level
    public static final Holder<MobEffect> ENFEEBLE = EFFECTS.register("enfeeble", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x5c452f));
    //nullifies invisibility
    public static final Holder<MobEffect> EXPOSED = EFFECTS.register("exposed", () -> new FootworkEffect(MobEffectCategory.NEUTRAL, 0x000000));
    //reduces stealth by 2 per level
    public static final Holder<MobEffect> REVEAL = EFFECTS.register("reveal", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xF74708).addAttributeModifier(FootworkAttributes.STEALTH, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "stealth_debuff"), -2, AttributeModifier.Operation.ADD_VALUE));
    //increases stealth by 2 per level
    public static final Holder<MobEffect> CONCEAL = EFFECTS.register("conceal", () -> new FootworkEffect(MobEffectCategory.BENEFICIAL, 0x949494).addAttributeModifier(FootworkAttributes.STEALTH, ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "stealth_buff"), 2, AttributeModifier.Operation.ADD_VALUE));
    //stuns are upgraded to knockdowns when unsteady
    public static final Holder<MobEffect> UNSTEADY = EFFECTS.register("unsteady", () -> new FootworkEffect(MobEffectCategory.NEUTRAL, 0x949494));

}
