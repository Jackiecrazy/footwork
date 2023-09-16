package jackiecrazy.footwork.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
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

import java.awt.*;

/**
 * Literally a copy-paste of AttackSweepParticle :P
 */
public class CustomSweepParticle extends TextureSheetParticle {

    protected final SpriteSet sprites;
    protected final float xzScale, yScale;
    protected final float xRot, yRot;
    private final ROTATIONTYPE type;

    CustomSweepParticle(ClientLevel level, double posX, double posY, double posZ, double dirX, double dirY, double dirZ, SpriteSet set, ROTATIONTYPE flat, double xScale, double yScale, int life, Color c) {
        super(level, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        this.xo = dirX;
        this.yo = dirY;
        this.zo = dirZ;
        xRot = (float) dirX;
        yRot = (float) dirY;

        //orientation.cross(Vector3f.YP);
        this.sprites = set;
        this.lifetime = life;
        float f = this.random.nextFloat() * 0.4F + 0.6F;
        this.rCol = f * c.getRed() / 255f;
        this.gCol = f * c.getGreen() / 255f;
        this.bCol = f * c.getBlue() / 255f;
        roll = Mth.PI / 2;
        xzScale = (float) xScale;
        this.yScale = (float) yScale;
        type = flat;
        quadSize = 1;

        this.setSpriteFromAge(set);
    }

    public void render(VertexConsumer p_107678_, Camera camera, float p_107680_) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(p_107680_, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(p_107680_, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(p_107680_, this.zo, this.z) - vec3.z());
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
        RenderSystem.disableCull();
        p_107678_.vertex(transforms[0].x(), transforms[0].y(), transforms[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(transforms[1].x(), transforms[1].y(), transforms[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(transforms[2].x(), transforms[2].y(), transforms[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(transforms[3].x(), transforms[3].y(), transforms[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

        p_107678_.vertex(transforms[3].x(), transforms[3].y(), transforms[3].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(transforms[2].x(), transforms[2].y(), transforms[2].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(transforms[1].x(), transforms[1].y(), transforms[1].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        p_107678_.vertex(transforms[0].x(), transforms[0].y(), transforms[0].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        RenderSystem.enableCull();
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
        /*if (age == 5 && !level.isEmptyBlock(new BlockPos(x, y - yScale / 2, z))) {
            for (int i = 0; i < 16; i++) {
                float radians = GeneralUtils.rad(i * 360f / 16f);

                final double sin = Mth.sin(radians) * 0.5;
                final double cos = Mth.cos(radians) * 0.5;
                level.addParticle(new DustParticleOptions(ParticleUtils.gravel, 1), x + cos, y, z + sin, cos, 0.0D, sin);
            }
        }*/
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public int getLightColor(float p_105562_) {
        return 15728880;
    }

    public boolean shouldCull() {
        return false;
    }

    public enum ROTATIONTYPE {
        NORMAL,
        FLAT,
        SUPERFLAT
    }

    @OnlyIn(Dist.CLIENT)
    public static class SweepProvider implements ParticleProvider<ScalingParticleType> {
        private final SpriteSet sprites;
        private final ROTATIONTYPE flat;

        public SweepProvider(SpriteSet set, ROTATIONTYPE flat) {
            this.sprites = set;
            this.flat = flat;
        }

        public Particle createParticle(ScalingParticleType type, ClientLevel world, double posX, double posY, double posZ, double dirX, double dirY, double dirZ) {
            if (type.getType() == FootworkParticles.IMPACT.get())
                return new ImpactParticle(world, posX, posY, posZ, dirX, dirY, dirZ, this.sprites, flat, type.getXSize(), type.getYSize(), type.getColor());
            return new CustomSweepParticle(world, posX, posY, posZ, dirX, dirY, dirZ, this.sprites, flat, type.getXSize(), type.getYSize(), type.getLife(), type.getColor());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class BonkProvider implements ParticleProvider<ScalingParticleType> {
        private final SpriteSet sprites;

        public BonkProvider(SpriteSet set) {
            this.sprites = set;
        }

        public Particle createParticle(ScalingParticleType type, ClientLevel world, double posX, double posY, double posZ, double dirX, double dirY, double dirZ) {
            return new CustomSweepParticle(world, posX, posY, posZ, dirX, dirY, dirZ, this.sprites, ROTATIONTYPE.NORMAL, type.getXSize(), type.getYSize(), type.getLife(), type.getColor());
        }
    }
}