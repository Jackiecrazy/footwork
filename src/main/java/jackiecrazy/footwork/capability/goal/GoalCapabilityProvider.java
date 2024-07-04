package jackiecrazy.footwork.capability.goal;

import jackiecrazy.footwork.Footwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class GoalCapabilityProvider {

    public static final EntityCapability<IGoalHelper, Void> CAP =
            EntityCapability.createVoid(
                    // Provide a name to uniquely identify the capability.
                    ResourceLocation.fromNamespaceAndPath(Footwork.MODID, "goal_provider"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    IGoalHelper.class);
    private static final IGoalHelper DUMMY = new GoalCapability();

    public static IGoalHelper getCap(LivingEntity le) {
        IGoalHelper ret = le.getCapability(CAP);
        if (ret != null) return ret;
        return DUMMY;
    }
}
