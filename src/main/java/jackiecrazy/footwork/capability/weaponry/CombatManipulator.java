package jackiecrazy.footwork.capability.weaponry;

import jackiecrazy.footwork.Footwork;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class CombatManipulator {

    public static ItemCapability<ICombatItemCapability, Void> CAP = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "combat_capability"), ICombatItemCapability.class);
}
