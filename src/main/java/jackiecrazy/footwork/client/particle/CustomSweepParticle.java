package jackiecrazy.footwork.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Literally a copy-paste of AttackSweepParticle :P
 */
public class CustomSweepParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    CustomSweepParticle(ClientLevel p_105546_, double p_105547_, double p_105548_, double p_105549_, double p_105550_, SpriteSet p_105551_, double scale) {
        super(p_105546_, p_105547_, p_105548_, p_105549_, 0.0D, 0.0D, 0.0D);
        this.sprites = p_105551_;
        this.lifetime = 7;
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = (float) scale + 1;
        this.setSpriteFromAge(p_105551_);
    }

    public void render(VertexConsumer p_107678_, Camera camera, float p_107680_) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) p_107680_, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) p_107680_, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) p_107680_, this.zo, this.z) - vec3.z());
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternion(camera.rotation());
            float f3 = Mth.lerp(p_107680_, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(p_107680_);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = this.getLightColor(p_107680_);
        p_107678_.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public int getLightColor(float p_105562_) {
        return 15728880;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ScalingParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet set) {
            this.sprites = set;
        }

        public Particle createParticle(ScalingParticleType type, ClientLevel world, double p_105579_, double p_105580_, double p_105581_, double p_105582_, double p_105583_, double p_105584_) {
            return new CustomSweepParticle(world, p_105579_, p_105580_, p_105581_, p_105582_, this.sprites, type.getSize());
        }
    }
}