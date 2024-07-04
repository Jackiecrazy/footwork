package jackiecrazy.footwork.capability.weaponry;

import jackiecrazy.footwork.Footwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class CombatManipulator {

    public static final ItemCapability<ICombatItemCapability, Void> CAP =
            ItemCapability.createVoid(
                    // Provide a name to uniquely identify the capability.
                    ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "item_combat_data"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    ICombatItemCapability.class);


    public static ICombatItemCapability getCap(ItemStack le) {
        return le.getCapability(CAP);//.orElseThrow(() -> new IllegalArgumentException("attempted to find a nonexistent capability"));
    }
}
