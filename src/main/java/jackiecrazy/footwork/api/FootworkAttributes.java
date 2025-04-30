package jackiecrazy.footwork.api;

import jackiecrazy.footwork.Footwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public class FootworkAttributes {

    public static final UUID[] MODIFIERS = {UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553da"), UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553db"), UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553dc"), UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553dd")};
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Footwork.MODID);
    public static final RegistryObject<Attribute> STEALTH = ATTRIBUTES.register("stealth", () -> new RangedAttribute(Footwork.MODID + ".stealth", 0d, -1024, 1024).setSyncable(true));
    public static final RegistryObject<Attribute> ENCIRCLEMENT_DISTANCE = ATTRIBUTES.register("encirclement_distance", () -> new RangedAttribute(Footwork.MODID + ".encirclement_distance", 0d, 0, 1024).setSyncable(true));

    public static final RegistryObject<Attribute> MAX_POSTURE = ATTRIBUTES.register("max_posture", () -> new RangedAttribute(Footwork.MODID + ".maxPosture", 0d, 0, 1024).setSyncable(true));
    public static final RegistryObject<Attribute> MAX_SPIRIT = ATTRIBUTES.register("max_spirit", () -> new RangedAttribute(Footwork.MODID + ".maxSpirit", 3d, 0, 1024).setSyncable(true));
    public static final RegistryObject<Attribute> DEATHBLOW_RESISTANCE = ATTRIBUTES.register("deathblow_resistance", () -> new RangedAttribute(Footwork.MODID + ".deathblow_res", 1d, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> RANK_GAIN = ATTRIBUTES.register("rank_gain", () -> new RangedAttribute(Footwork.MODID + ".rank_gain", 1d, 0, 1024).setSyncable(true));
    public static final RegistryObject<Attribute> ADRENALINE_GAIN = ATTRIBUTES.register("adrenaline_gain", () -> new RangedAttribute(Footwork.MODID + ".adrenaline_gain", 1d, 0, 1024).setSyncable(true));
    public static final RegistryObject<Attribute> RALLY_PERCENTAGE = ATTRIBUTES.register("rally_percentage", () -> new RangedAttribute(Footwork.MODID + ".rally_percentage", 0d, 0, 1).setSyncable(true));
    public static final RegistryObject<Attribute> RALLY_DURATION = ATTRIBUTES.register("rally_duration", () -> new RangedAttribute(Footwork.MODID + ".rally_duration", 2d, 0, 60).setSyncable(true));

    public static final RegistryObject<Attribute> SKILL_EFFECTIVENESS = ATTRIBUTES.register("skill_effectiveness", () -> new RangedAttribute(Footwork.MODID + ".skill_effectiveness", 1d, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> TWO_HANDING = ATTRIBUTES.register("two_handing", () -> new RangedAttribute(Footwork.MODID + ".two_handing", 0d, -1, 4).setSyncable(true));

}
