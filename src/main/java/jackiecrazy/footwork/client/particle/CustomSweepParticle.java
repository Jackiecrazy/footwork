package jackiecrazy.footwork.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
    private final ROTATIONTYPE type;
    private final float xzScale, yScale;

    private final float xRot, yRot;

    CustomSweepParticle(ClientLevel level, double posX, double posY, double posZ, double dirX, double dirY, double dirZ, SpriteSet set, ROTATIONTYPE flat, double xScale, double yScale) {
        super(level, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        this.xo = dirX;
        this.yo = dirY;
        this.zo = dirZ;
        xRot = (float) dirX;
        yRot = (float) dirY;

        //orientation.cross(Vector3f.YP);
        this.sprites = set;
        this.lifetime = 10;
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        roll = Mth.PI / 2;
        xzScale = (float) xScale;
        this.yScale = (float) yScale;
        type = flat;
        quadSize = 1;

        this.setSpriteFromAge(set);
    }

    public void render(VertexConsumer p_107678_, Camera camera, float p_107680_) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) p_107680_, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) p_107680_, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) p_107680_, this.zo, this.z) - vec3.z());
        Quaternion quaternion;
        switch (type) {
            case FLAT -> {
                quaternion = new Quaternion(0, 0, 0, 1);
                quaternion.mul(Vector3f.YP.rotationDegrees(-yRot));
                quaternion.mul(Vector3f.XP.rotationDegrees(xRot));
                quaternion.mul(Vector3f.XP.rotation(1.4f));
            }
            case SUPERFLAT -> quaternion = new Quaternion(Vector3f.XP.rotation(Mth.PI / 2));
            default -> quaternion = camera.rotation();
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);

        float scale = this.getQuadSize(p_107680_);
        Vector3f[] transforms = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)};
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = transforms[i];
            vector3f.mul(xzScale, yScale, xzScale);
            vector3f.transform(quaternion);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = this.getLightColor(p_107680_);
        p_107678_.vertex((double) transforms[0].x(), (double) transforms[0].y(), (double) transforms[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) transforms[1].x(), (double) transforms[1].y(), (double) transforms[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) transforms[2].x(), (double) transforms[2].y(), (double) transforms[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) transforms[3].x(), (double) transforms[3].y(), (double) transforms[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

        p_107678_.vertex((double) transforms[3].x(), (double) transforms[3].y(), (double) transforms[3].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) transforms[2].x(), (double) transforms[2].y(), (double) transforms[2].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) transforms[1].x(), (double) transforms[1].y(), (double) transforms[1].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex((double) transforms[0].x(), (double) transforms[0].y(), (double) transforms[0].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
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

    public enum ROTATIONTYPE {
        NORMAL,
        FLAT,
        SUPERFLAT
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ScalingParticleType> {
        private final SpriteSet sprites;
        private final ROTATIONTYPE flat;

        public Provider(SpriteSet set, ROTATIONTYPE flat) {
            this.sprites = set;
            this.flat = flat;
        }

        public Particle createParticle(ScalingParticleType type, ClientLevel world, double posX, double posY, double posZ, double dirX, double dirY, double dirZ) {
            return new CustomSweepParticle(world, posX, posY, posZ, dirX, dirY, dirZ, this.sprites, flat, type.getXSize(), type.getYSize());
        }
    }
}