package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.api.FootworkAttributes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ThreatUtils {
    /*
        0. pests, silverfish
        1. critters, bats
        2. animals, pigs
        4. fodder hostiles, zombies
        6. elite mobs, enderman
        10. minibosses and players
        14. wandering bosses
        15. bosses, naga
        19. elite bosses, enderdragon
        20. gods
        21. godslayers
         */
    public static double getThreat(LivingEntity e) {
        return GeneralUtils.getAttributeValueSafe(e, FootworkAttributes.THREAT.get());
    }
}
