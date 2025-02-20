package jackiecrazy.footwork.potion;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.api.FootworkAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FootworkEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Footwork.MODID);

    //slows posture regeneration by 0.2 per level
    public static final RegistryObject<MobEffect> EXHAUSTION = EFFECTS.register("exhaustion", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xa9a9a9));
    //next attack against a distracted creature applies the distracted bonus and removes distraction
    public static final RegistryObject<MobEffect> DISTRACTION = EFFECTS.register("distraction", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xc98fff));
    //identical to distraction, but not removed on attack
    public static final RegistryObject<MobEffect> CONFUSION = EFFECTS.register("confusion", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x7833b0));
    //identical to distraction, the target will attempt to run away, invoke with EffectUtils.causeFear() for a specific target, or invoke terror and let it handle the rest.
    public static final RegistryObject<MobEffect> FEAR = EFFECTS.register("fear", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xfcfc00));
    //use this to quickly give fear to a target entity.
    public static final RegistryObject<MobEffect> TERROR = EFFECTS.register("terror", TerrorEffect::new);
    //attacks against paralyzed targets apply the unaware bonus, paralyzed targets cannot move
    public static final RegistryObject<MobEffect> PARALYSIS = EFFECTS.register("paralysis", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x2c2c2c).addAttributeModifier(Attributes.MOVEMENT_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
    //identical to paralysis, but adds 4 armor
    public static final RegistryObject<MobEffect> PETRIFY = EFFECTS.register("petrify", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x949494).addAttributeModifier(Attributes.MOVEMENT_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -1, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "55FCED67-E92A-486E-9800-B47F202C4386", 100, AttributeModifier.Operation.ADDITION).addAttributeModifier(Attributes.ARMOR, "55FCED67-E92A-486E-9800-B47F202C4386", 4, AttributeModifier.Operation.ADDITION));
    //identical to paralysis, but is reduced by 1 second for every point of incoming damage
    public static final RegistryObject<MobEffect> SLEEP = EFFECTS.register("sleep", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xc0c0d3e).addAttributeModifier(Attributes.MOVEMENT_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
    //increases incoming non-combat damage by potency
    public static final RegistryObject<MobEffect> VULNERABLE = EFFECTS.register("vulnerable", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x73a915));
    //reduces armor by 2 per level
    public static final RegistryObject<MobEffect> CORROSION = EFFECTS.register("corrosion", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xcfd102).addAttributeModifier(Attributes.ARMOR, "55FCED67-E92A-486E-9800-B47F202C4386", -2, AttributeModifier.Operation.ADDITION));
    //increases armor by 2 per level
    public static final RegistryObject<MobEffect> FORTIFICATION = EFFECTS.register("fortification", () -> new FootworkEffect(MobEffectCategory.BENEFICIAL, 0x0eb00c).addAttributeModifier(Attributes.ARMOR, "55FCED67-E92B-486E-9800-B47F202C4386", 2, AttributeModifier.Operation.ADDITION));
    //increases posture damage by 20% per level
    public static final RegistryObject<MobEffect> ENFEEBLE = EFFECTS.register("enfeeble", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0x5c452f));
    //nullifies invisibility
    public static final RegistryObject<MobEffect> EXPOSED = EFFECTS.register("exposed", () -> new FootworkEffect(MobEffectCategory.NEUTRAL, 0x000000));
    //reduces stealth by 2 per level
    public static final RegistryObject<MobEffect> REVEAL = EFFECTS.register("reveal", () -> new FootworkEffect(MobEffectCategory.HARMFUL, 0xF74708).addAttributeModifier(FootworkAttributes.STEALTH.get(), "55FCED67-D92A-486E-9800-B47F202C4386", -2, AttributeModifier.Operation.ADDITION));
    //increases stealth by 2 per level
    public static final RegistryObject<MobEffect> CONCEAL = EFFECTS.register("conceal", () -> new FootworkEffect(MobEffectCategory.BENEFICIAL, 0x949494).addAttributeModifier(FootworkAttributes.STEALTH.get(), "55FCED67-E92A-486E-9800-B47F202C4386", 2, AttributeModifier.Operation.ADDITION));
    //stuns are upgraded to knockdowns when unsteady
    public static final RegistryObject<MobEffect> UNSTEADY = EFFECTS.register("unsteady", () -> new FootworkEffect(MobEffectCategory.NEUTRAL, 0x949494));

}
