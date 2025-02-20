package jackiecrazy.footwork.capability.weaponry;

import net.minecraft.world.item.ItemStack;

public class CombatManipulator {

    public static Capability<ICombatItemCapability> CAP = null;

    public static LazyOptional<ICombatItemCapability> getCap(ItemStack le) {
        return le.getCapability(CAP);//.orElseThrow(() -> new IllegalArgumentException("attempted to find a nonexistent capability"));
    }
}
