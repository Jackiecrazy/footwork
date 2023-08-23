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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class ParticleUtils {
    public static final Vector3f gravel = new Vector3f(Vec3.fromRGB24(-8356741));

    public static void playSweepParticle(ParticleType<ScalingParticleType> type, LivingEntity e, Vec3 vec, int angle, double size, Color c, double vertFromFoot) {
        double xSize = size, ySize = 1;
        int time = 8;
        if (type == FootworkParticles.IMPACT.get()) {
            xSize = size;
        }
        if (type == FootworkParticles.CIRCLE.get()) {
            ySize = size;
            time = 14;
        }
        if (type == FootworkParticles.CLEAVE.get()) {
            xSize = 1;
            ySize = size;
            time = 7;
        }
        final float rotated = e.getYRot() + angle;
//        double d0 = horDistScale * -Mth.sin(rotated * ((float) Math.PI / 180F));
//        double d1 = horDistScale * Mth.cos(rotated * ((float) Math.PI / 180F));
        if (e.level instanceof ServerLevel s) {
            s.sendParticles(new ScalingParticleType(type, xSize, ySize, time, c), vec.x, vec.y + vertFromFoot, vec.z, 0, e.getXRot(), e.getYRot(), 0, 1.0D);
        }

    }

    public static void playBonkParticle(Level l, Vec3 vec, double size, double drift, int life, Color c) {
        size /= 2;
        if (l instanceof ServerLevel s) {
            int num = drift > 0 ? 1 : 0;
            s.sendParticles(new ScalingParticleType(FootworkParticles.BONK.get(), size, size, life, c), vec.x, vec.y, vec.z, num, drift, drift, drift, 0.0D);
        }

    }

    public static void playSweepParticle(ParticleType<ScalingParticleType> type, LivingEntity e, Vec3 vec, int angle, double size, double vertFromFoot) {
        playSweepParticle(type, e, e.position(), angle, size, Color.WHITE, vertFromFoot);
    }

    public static void playSweepParticle(ParticleType<ScalingParticleType> type, LivingEntity e, int angle, double size, double vertFromFoot) {
        playSweepParticle(type, e, e.position(), angle, size, vertFromFoot);
    }


}
