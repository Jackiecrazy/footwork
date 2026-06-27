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

}
