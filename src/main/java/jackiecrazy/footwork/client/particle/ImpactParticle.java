package jackiecrazy.footwork.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import jackiecrazy.footwork.utils.GeneralUtils;
import jackiecrazy.footwork.utils.ParticleUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class ImpactParticle extends CustomSweepParticle {
    ImpactParticle(ClientLevel level, double posX, double posY, double posZ, double dirX, double dirY, double dirZ, SpriteSet set, ROTATIONTYPE flat, double xScale, double yScale, Color c) {
        super(level, posX, posY + 0.7, posZ, dirX, dirY, dirZ, set, flat, xScale, yScale, 7, c);
    }

    public void render(VertexConsumer p_107678_, Camera camera, float p_107680_) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(p_107680_, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(p_107680_, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(p_107680_, this.zo, this.z) - vec3.z());
        Quaternionf quaternion = camera.rotation();

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.rotate(quaternion);

        float scale = this.getQuadSize(p_107680_);
        Vector3f[] transforms = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)};
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = transforms[i];
            vector3f.mul(1, 1, 1);
            vector3f.rotate(quaternion);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = this.getLightColor(p_107680_);
        p_107678_.addVertex(transforms[0].x(), transforms[0].y(), transforms[0].z()).setUv(f8, f6).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        p_107678_.addVertex(transforms[1].x(), transforms[1].y(), transforms[1].z()).setUv(f8, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        p_107678_.addVertex(transforms[2].x(), transforms[2].y(), transforms[2].z()).setUv(f7, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        p_107678_.addVertex(transforms[3].x(), transforms[3].y(), transforms[3].z()).setUv(f7, f6).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);

        p_107678_.addVertex(transforms[3].x(), transforms[3].y(), transforms[3].z()).setUv(f8, f6).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        p_107678_.addVertex(transforms[2].x(), transforms[2].y(), transforms[2].z()).setUv(f8, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        p_107678_.addVertex(transforms[1].x(), transforms[1].y(), transforms[1].z()).setUv(f7, f5).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        p_107678_.addVertex(transforms[0].x(), transforms[0].y(), transforms[0].z()).setUv(f7, f6).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
    }

    public void tick() {
        super.tick();
        if (age == 4) {
            for (int i = 0; i < 32; i++) {
                float radians = GeneralUtils.rad(i * 360f / 32f);

                final double sin = Mth.sin(radians) * xzScale;
                final double cos = Mth.cos(radians) * xzScale;
                level.addParticle(new DustParticleOptions(ParticleUtils.gravel, 1), x + cos, y - 0.7, z + sin, 0, 0.0D, 0);
            }
        }
    }
}
