package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.event.EntityAwarenessEvent;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class StealthUtils {
    public static StealthUtils INSTANCE=new StealthUtils();//this will be usurped by the stealth one when appropriate
    public Awareness getAwareness(LivingEntity attacker, LivingEntity target) {
        //essentially a dummy
        EntityAwarenessEvent eae = new EntityAwarenessEvent(target, attacker, Awareness.ALERT);
        MinecraftForge.EVENT_BUS.post(eae);
        return eae.getAwareness();
    }

    public static boolean inWeb(LivingEntity e) {
        if (!e.level.isAreaLoaded(e.blockPosition(), (int) Math.ceil(e.getBbWidth()))) return false;
        double minX = e.getX() - e.getBbWidth() / 2, minY = e.getY(), minZ = e.getZ() - e.getBbWidth() / 2;
        double maxX = e.getX() + e.getBbWidth() / 2, maxY = e.getY() + e.getBbHeight(), maxZ = e.getZ() + e.getBbWidth() / 2;
        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    if (e.level.getBlockState(e.blockPosition()).getMaterial().equals(Material.WEB))
                        return true;
                }
            }
        }
        return false;
    }

    public static int getActualLightLevel(World world, BlockPos pos) {
        int i = 0;
        if (world.dimensionType().hasSkyLight()) {
            world.updateSkyBrightness();
            int dark = world.getSkyDarken();
            i = world.getBrightness(LightType.SKY, pos) - dark;
        }

        i = MathHelper.clamp(Math.max(world.getBrightness(LightType.BLOCK, pos), i), 0, 15);
        return i;
    }

    public enum Awareness {
        UNAWARE,//cannot be parried, absorbed, shattered, or deflected
        DISTRACTED,//deals extra (posture) damage
        ALERT//normal damage and reduction
    }
}
