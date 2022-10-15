package jackiecrazy.footwork.api;

import jackiecrazy.footwork.Footwork;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public class WarAttributes {
    public static final UUID[] MODIFIERS = {
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553da"),
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553db"),
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553dc"),
            UUID.fromString("a516026a-bee2-4014-bcb6-b6a5775553dd")
    };
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Footwork.MODID);

    public static final RegistryObject<Attribute> ABSORPTION = ATTRIBUTES.register("absorption", () -> new RangedAttribute(Footwork.MODID + ".absorption", 0d, 0, Double.MAX_VALUE).m_22084_(true));
    public static final RegistryObject<Attribute> DEFLECTION = ATTRIBUTES.register("deflection", () -> new RangedAttribute(Footwork.MODID + ".deflection", 0d, 0, 60).m_22084_(true));
    public static final RegistryObject<Attribute> SHATTER = ATTRIBUTES.register("shatter", () -> new RangedAttribute(Footwork.MODID + ".shatter", 0d, 0, Double.MAX_VALUE).m_22084_(true));
    public static final RegistryObject<Attribute> STEALTH = ATTRIBUTES.register("stealth", () -> new RangedAttribute(Footwork.MODID + ".stealth", 10d, -Double.MAX_VALUE, Double.MAX_VALUE).m_22084_(true));
    public static final RegistryObject<Attribute> MAX_POSTURE = ATTRIBUTES.register("max_posture", () -> new RangedAttribute(Footwork.MODID + ".maxPosture", 0d, 0, Double.MAX_VALUE));
    public static final RegistryObject<Attribute> MAX_SPIRIT = ATTRIBUTES.register("max_spirit", () -> new RangedAttribute(Footwork.MODID + ".maxSpirit", 10d, 0, Double.MAX_VALUE));
    public static final RegistryObject<Attribute> MAX_MIGHT = ATTRIBUTES.register("max_might", () -> new RangedAttribute(Footwork.MODID + ".maxMight", 10d, 0, Double.MAX_VALUE));
    public static final RegistryObject<Attribute> POSTURE_REGEN = ATTRIBUTES.register("posture_regen", () -> new RangedAttribute(Footwork.MODID + ".postureGen", 0d, -Double.MAX_VALUE, Double.MAX_VALUE));
    public static final RegistryObject<Attribute> SPIRIT_REGEN = ATTRIBUTES.register("spirit_regen", () -> new RangedAttribute(Footwork.MODID + ".spiritGen", 10d, -Double.MAX_VALUE, Double.MAX_VALUE));
    public static final RegistryObject<Attribute> MIGHT_GEN = ATTRIBUTES.register("might_gen", () -> new RangedAttribute(Footwork.MODID + ".mightGen", 10d, -Double.MAX_VALUE, Double.MAX_VALUE));
    public static final RegistryObject<Attribute> BARRIER = ATTRIBUTES.register("barrier", () -> new RangedAttribute(Footwork.MODID + ".barrier", 0.0d, 0, 1).m_22084_(true));
    public static final RegistryObject<Attribute> BARRIER_COOLDOWN = ATTRIBUTES.register("barrier_cooldown", () -> new RangedAttribute(Footwork.MODID + ".barrierCooldown", 0d, 0, Integer.MAX_VALUE).m_22084_(true));
}
