package jackiecrazy.footwork.capability.resources;

import jackiecrazy.footwork.Footwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class CombatData {
    private static ICombatCapability OHNO = new DummyCombatCap();

    public static final EntityCapability<ICombatCapability, Void> CAP =
            EntityCapability.createVoid(
                    // Provide a name to uniquely identify the capability.
                    ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "combat_data"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    ICombatCapability.class);

    public static ICombatCapability getCap(LivingEntity le) {
        ICombatCapability ret = le.getCapability(CAP);
        if (ret != null) return ret;
        return OHNO;
    }
}
