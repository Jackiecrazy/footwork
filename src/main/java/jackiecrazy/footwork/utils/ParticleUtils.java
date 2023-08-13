package jackiecrazy.footwork.utils;

import com.mojang.math.Vector3f;
import jackiecrazy.footwork.client.particle.FootworkParticles;
import jackiecrazy.footwork.client.particle.ScalingParticleType;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
    public static final Vector3f gravel = new Vector3f(Vec3.fromRGB24(-8356741));

    public static void playSweepParticle(ParticleType<ScalingParticleType> type, LivingEntity e, Vec3 vec, int angle, double size, double vertFromFoot) {
        double xSize = size, ySize = 1;
        if (type == FootworkParticles.IMPACT.get()) {
            xSize = size;
        }
        if (type == FootworkParticles.CIRCLE.get()) ySize = size;
        if (type == FootworkParticles.CLEAVE.get()) {
            xSize = 1;
            ySize = size;
        }
        final float rotated = e.getYRot() + angle;
//        double d0 = horDistScale * -Mth.sin(rotated * ((float) Math.PI / 180F));
//        double d1 = horDistScale * Mth.cos(rotated * ((float) Math.PI / 180F));
        if (e.level instanceof ServerLevel s) {
            //0 means no drift
            s.sendParticles(new ScalingParticleType(type, xSize, ySize), vec.x, vec.y + vertFromFoot, vec.z, 0, e.getXRot(), e.getYRot(), 0, 1.0D);
//            if (splash) {
//                for (int i = 0; i < 32; i++) {
//                    float radians = GeneralUtils.rad(i * 360f / 32f);
//                    final double sin = Mth.sin(radians) * size;
//                    final double cos = Mth.cos(radians) * size;
//                    s.sendParticles(new DustParticleOptions(gravel, 1), vec.x + cos, vec.y + vertFromFoot, vec.z + sin, 0, cos, 0.0D, sin, 10.0D);
//                }
//            }
        }

    }

    public static void playSweepParticle(ParticleType<ScalingParticleType> type, LivingEntity e, int angle, double horDistScale, double vertFromFoot) {
        playSweepParticle(type, e, e.position(), angle, horDistScale, vertFromFoot);
    }


}
