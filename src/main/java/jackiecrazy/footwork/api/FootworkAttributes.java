package jackiecrazy.footwork.api;

import jackiecrazy.footwork.Footwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public class FootworkAttributes {
    public static final UUID[] MODIFIERS = {
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553da"),
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553db"),
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553dc"),
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553dd")
    };
    /*
    list of attributes:
max spirit, max posture, deathblow counter, rank gain, adrenaline gain, rally percentage, rally duration
     */
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Footwork.MODID);
    public static final DeferredHolder<Attribute, Attribute> STEALTH = ATTRIBUTES.register("stealth", () -> new RangedAttribute(Footwork.MODID + ".stealth", 0d, -1024, 1024).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> ENCIRCLEMENT_DISTANCE = ATTRIBUTES.register("encirclement_distance", () -> new RangedAttribute(Footwork.MODID + ".encirclement_distance", 0d, 0, 1024).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> EVASION = ATTRIBUTES.register("evasion", () -> new RangedAttribute(Footwork.MODID + ".evasion", 0, 0, 100).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MAX_FRACTURE = ATTRIBUTES.register("max_fracture", () -> new RangedAttribute(Footwork.MODID + ".maxFracture", 0, 0, 1024).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MAX_POSTURE = ATTRIBUTES.register("max_posture", () -> new RangedAttribute(Footwork.MODID + ".maxPosture", 0d, 0, 1024).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> POSTURE_COOLDOWN = ATTRIBUTES.register("posture_cooldown_speed", () -> new RangedAttribute(Footwork.MODID + ".postureSpeed", 1d, 0, 1024));
    public static final DeferredHolder<Attribute, Attribute> POSTURE_REGEN = ATTRIBUTES.register("posture_regen", () -> new RangedAttribute(Footwork.MODID + ".postureGen", 10d, -1024, 1024));
    public static final DeferredHolder<Attribute, Attribute> POSTURE_GAIN = ATTRIBUTES.register("posture_gain", () -> new RangedAttribute(Footwork.MODID + ".postureHeal", 1d, -1024, 1024));
    public static final DeferredHolder<Attribute, Attribute> MAX_SPIRIT = ATTRIBUTES.register("max_spirit", () -> new RangedAttribute(Footwork.MODID + ".maxSpirit", 3d, 0, 1024).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> SPIRIT_COOLDOWN = ATTRIBUTES.register("spirit_cooldown_speed", () -> new RangedAttribute(Footwork.MODID + ".spiritSpeed", 1d, 0, 1024));
    public static final DeferredHolder<Attribute, Attribute> SPIRIT_REGEN = ATTRIBUTES.register("spirit_regen", () -> new RangedAttribute(Footwork.MODID + ".spiritGen", 0.5d, -1024, 1024));
    public static final DeferredHolder<Attribute, Attribute> SPIRIT_GAIN = ATTRIBUTES.register("spirit_gain", () -> new RangedAttribute(Footwork.MODID + ".spiritHeal", 1d, -1024, 1024));
    public static final DeferredHolder<Attribute, Attribute> MAX_MIGHT = ATTRIBUTES.register("max_might", () -> new RangedAttribute(Footwork.MODID + ".maxMight", 2d, 0, 1024).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> MIGHT_GRACE = ATTRIBUTES.register("might_grace", () -> new RangedAttribute(Footwork.MODID + ".mightSpeed", 1d, 0, 1024));
    public static final DeferredHolder<Attribute, Attribute> MIGHT_GEN = ATTRIBUTES.register("might_gen", () -> new RangedAttribute(Footwork.MODID + ".mightGen", 1d, -1024, 1024));

    public static final DeferredHolder<Attribute, Attribute> THREAT = ATTRIBUTES.register("threat", () -> new RangedAttribute(Footwork.MODID + ".threat", 5d, 0, 100).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> SKILL_EFFECTIVENESS = ATTRIBUTES.register("skill_effectiveness", () -> new RangedAttribute(Footwork.MODID + ".skill_effectiveness", 1d, 0, 100).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> TWO_HANDING = ATTRIBUTES.register("two_handing", () -> new RangedAttribute(Footwork.MODID + ".two_handing", 0d, -1, 4).setSyncable(true));
}
