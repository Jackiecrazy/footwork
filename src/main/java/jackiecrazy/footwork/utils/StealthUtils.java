package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.event.EntityAwarenessEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
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
        if (!e.f_19853_.isAreaLoaded(e.m_142538_(), (int) Math.ceil(e.m_20205_()))) return false;
        double minX = e.m_20185_() - e.m_20205_() / 2, minY = e.m_20186_(), minZ = e.m_20189_() - e.m_20205_() / 2;
        double maxX = e.m_20185_() + e.m_20205_() / 2, maxY = e.m_20186_() + e.m_20206_(), maxZ = e.m_20189_() + e.m_20205_() / 2;
        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    if (e.f_19853_.m_8055_(e.m_142538_()).m_60767_().equals(Material.f_76311_))
                        return true;
                }
            }
        }
        return false;
    }

    public static int getActualLightLevel(Level world, BlockPos pos) {
        int i = 0;
        if (world.m_6042_().m_63935_()) {
            world.m_46465_();
            int dark = world.m_7445_();
            i = world.m_45517_(LightLayer.SKY, pos) - dark;
        }

        i = Mth.m_14045_(Math.max(world.m_45517_(LightLayer.BLOCK, pos), i), 0, 15);
        return i;
    }

    public enum Awareness {
        UNAWARE,//cannot be parried, absorbed, shattered, or deflected
        DISTRACTED,//deals extra (posture) damage
        ALERT//normal damage and reduction
    }
}
