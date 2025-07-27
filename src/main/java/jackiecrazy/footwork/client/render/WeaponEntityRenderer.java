package jackiecrazy.footwork.client.render;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import jackiecrazy.footwork.entity.flyingweapon.FlyingWeaponEntity;
import jackiecrazy.footwork.entity.flyingweapon.SwingHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Deque;
import java.util.List;

public class WeaponEntityRenderer extends EntityRenderer<FlyingWeaponEntity> {
    public static final Vector3f NORMAL = new Vector3f(0, 1, 0);
    private final ItemRenderer itemRenderer;

    public WeaponEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FlyingWeaponEntity entity,
                       float yaw,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight) {
        if (buffer instanceof OutlineBufferSource obs) {
            obs.setColor((int) (Math.sin(Math.toRadians(entity.tickCount)) * 128) + 128, 256 - (int) (Math.sin(Math.toRadians(entity.tickCount)) * 128) + 128, (int) (Math.cos(Math.toRadians(entity.tickCount)) * 128) + 128, 255);
        }
        ItemStack stack = entity.getHeldItem();
        if (!stack.isEmpty()) {

            //actual weapon render//
            poseStack.pushPose();
            // Position and rotate as needed
            float lerpYRot = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
            float lerpXRot = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
            float lerpZRot = Mth.rotLerp(partialTicks, entity.rollO, entity.getRoll());
            //float lerpDisplacement = Mth.rotLerp(partialTicks, entity.displacementO, entity.getDisplacementForRender());
            poseStack.mulPose(Axis.YP.rotationDegrees(-lerpYRot)); // Yaw
            poseStack.mulPose(Axis.XP.rotationDegrees(-lerpXRot));  // Pitch
            poseStack.mulPose(Axis.ZP.rotationDegrees(lerpZRot));  // Roll
            poseStack.translate(0, 0, -0.4);//adjust weapon offset so it's at the middle
            //poseStack.translate(0, 0, -0.8);//adjust weapon offset so the tip is roughly at the entity
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
            poseStack.mulPose(Axis.XP.rotationDegrees(100));//this rotates a standard iron sword perfectly horizontal
            // Scale and render
            float scale = (float) Math.max(entity.attackRange / 3, 0.4);
            poseStack.scale(1f, scale, scale);

            //actual weapon
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            this.itemRenderer.renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, 0xF000F0, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), 0);
            //renderShadowWeapon(stack, poseStack, buffer);
            poseStack.popPose();

            //afterimage render//
            //renderAfterimages(entity, partialTicks, poseStack, buffer, packedLight, stack);
            renderBigAfterimage(entity, partialTicks, poseStack, buffer, 0xF000F0, stack);
            //renderTrail(entity, poseStack, partialTicks, buffer);
            renderTrailIGuess(entity, poseStack, partialTicks, buffer);
        }
    }

    private void renderBigAfterimage(FlyingWeaponEntity entity,
                                     float partialTicks,
                                     PoseStack poseStack,
                                     MultiBufferSource buffer,
                                     int packedLight,
                                     ItemStack stack) {
        poseStack.pushPose();
        SwingHistory from;
        SwingHistory to = null;
        //cancel all natural entity offsets first
        Vec3 interpolated = entity.getPosition(partialTicks); // same as what's applied by default
        poseStack.translate(
                -(interpolated.x),
                -(interpolated.y),
                -(interpolated.z)
        );
        int skip = 0;
        if (entity.renderLag > 0) {
            float lerpRenderLag = (Mth.lerp(partialTicks, entity.renderLagO, entity.renderLag) * FlyingWeaponEntity.CLIENT_SMOOTHING_SUBTICKS);
            for (Tuple<SwingHistory,SwingHistory> sh : entity.getTrailHistory()) {
                from = to;
                to = sh.getB();
                skip += 1;
                if (from != null && to != null && skip >= lerpRenderLag) {
                    poseStack.pushPose();

                    float moddedPartialTicks = (partialTicks % (1f / FlyingWeaponEntity.CLIENT_SMOOTHING_SUBTICKS))*FlyingWeaponEntity.CLIENT_SMOOTHING_SUBTICKS;
                    Vec3 interpolate = from.position().lerp(to.position(), moddedPartialTicks);
                    poseStack.translate(interpolate.x, interpolate.y, interpolate.z);
                    // Position and rotate as needed
                    float lerpYRot = Mth.rotLerp(moddedPartialTicks, from.pitch(), to.pitch());
                    float lerpXRot = Mth.rotLerp(moddedPartialTicks, from.yaw(), to.yaw());
                    float lerpZRot = Mth.rotLerp(moddedPartialTicks, from.roll(), to.roll());
                    //float lerpDisplacement = Mth.rotLerp(partialTicks, entity.displacementO, entity.getDisplacementForRender());
                    poseStack.mulPose(Axis.YP.rotationDegrees(-lerpYRot)); // Yaw
                    poseStack.mulPose(Axis.XP.rotationDegrees(-lerpXRot));  // Pitch, +angle to point the sword
                    poseStack.mulPose(Axis.ZP.rotationDegrees(lerpZRot));  // Roll
                    poseStack.translate(0, 0, -0.1 * entity.attackRange);//pull pommel back a bit
                    float scale = (float) Math.max(entity.attackRange, 0.4);
                    poseStack.scale(scale, scale, scale);//should scale here, right?
                    poseStack.translate(0, 0, -0.4);//aligning pommel to the best of my ability
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
                    poseStack.mulPose(Axis.XP.rotationDegrees(100));//this rotates a standard iron sword perfectly horizontal
                    poseStack.translate(0.15 * entity.attackRange, 0, 0);//trying to put the weapon on the same axis

                    //afterimages
//                    MultiBufferSource bufferWithAlpha = new AlphaMultiBufferSource(buffer, alpha);
//                    itemRenderer.render(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferWithAlpha, packedLight, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null, 0));
                    int alpha = (int)(lerpRenderLag * 32 / FlyingWeaponEntity.CLIENT_SMOOTHING_SUBTICKS);
                    renderShadowWeapon(stack, poseStack, buffer, alpha);
                    poseStack.popPose();
                    break;
                }
            }
        }
        poseStack.popPose();
    }

    private void renderAfterimages(FlyingWeaponEntity entity,
                                   float partialTicks,
                                   PoseStack poseStack,
                                   MultiBufferSource buffer,
                                   int packedLight,
                                   ItemStack stack) {
        poseStack.pushPose();
        float alpha = 1f;
        SwingHistory sh0;
        SwingHistory sh1 = null;
        //cancel all natural entity offsets first
        Vec3 interpolated = entity.getPosition(partialTicks); // same as what's applied by default
        poseStack.translate(
                -(interpolated.x),
                -(interpolated.y),
                -(interpolated.z)
        );
        int skip = 0;
        for (Tuple<SwingHistory, SwingHistory> sh : entity.getTrailHistory()) {
            sh0 = sh1;
            sh1 = sh.getA();
            skip += 1;
            if (sh0 != null && sh1 != null && skip >= entity.renderLag) {
                skip %= 4;
                alpha *= 0.5f;
                renderAfterimage(entity, partialTicks, poseStack, buffer, packedLight, stack, sh0, sh1, alpha);
            }
        }
        poseStack.popPose();
    }

    private void renderShadowWeapon(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int alpha) {
        //VertexConsumer consumer = bufferSource.getBuffer(FootworkRenderTypes.GHOST_ITEM);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
        VertexConsumer whiteTinted = new VertexConsumerWrapper(consumer) {
            @Override
            public VertexConsumer color(int r, int g, int b, int a) {
                return super.color(0, 0, 0, alpha); // force semi-translucent black
            }
        };
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
        model.applyTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, poseStack, false);

        itemRenderer.renderModelLists(
                model,
                stack,
                0xF000F0,//max brightness
                OverlayTexture.NO_OVERLAY,
                poseStack,
                whiteTinted
        );

    }

    private void renderAfterimage(FlyingWeaponEntity entity,
                                  float partialTicks,
                                  PoseStack poseStack,
                                  MultiBufferSource buffer,
                                  int packedLight,
                                  ItemStack stack,
                                  SwingHistory from,
                                  SwingHistory to,
                                  float alpha) {
        poseStack.pushPose();

        Vec3 interpolate = from.position().lerp(to.position(), partialTicks);
        poseStack.translate(interpolate.x, interpolate.y, interpolate.z);
        // Position and rotate as needed
        float lerpYRot = Mth.rotLerp(partialTicks, from.pitch(), to.pitch());
        float lerpXRot = Mth.rotLerp(partialTicks, from.yaw(), to.yaw());
        float lerpZRot = Mth.rotLerp(partialTicks, from.roll(), to.roll());
        //float lerpDisplacement = Mth.rotLerp(partialTicks, entity.displacementO, entity.getDisplacementForRender());
        poseStack.mulPose(Axis.YP.rotationDegrees(-lerpYRot)); // Yaw
        poseStack.mulPose(Axis.XP.rotationDegrees(-lerpXRot));  // Pitch, +angle to point the sword
        poseStack.mulPose(Axis.ZP.rotationDegrees(lerpZRot));  // Roll
        poseStack.translate(0, 0, -0.8);//adjust weapon offset so the tip is roughly at the entity
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
        poseStack.mulPose(Axis.XP.rotationDegrees(100));//this rotates a standard iron sword perfectly horizontal

        // Scale and render
        float scale = (float) Math.max(entity.attackRange / 3, 0.4);
        poseStack.scale(scale, scale, scale);

        //afterimages
        MultiBufferSource bufferWithAlpha = new AlphaMultiBufferSource(buffer, alpha);
        itemRenderer.render(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferWithAlpha, packedLight, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null, 0));
        poseStack.popPose();
    }

    private void renderTrail(FlyingWeaponEntity entity,
                             PoseStack poseStack,
                             float partialticks,
                             MultiBufferSource buffer) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(FootworkRenderTypes.white));
        Vec3 interpolated = entity.getPosition(partialticks); // same as what's applied by default
        poseStack.translate(
                -(interpolated.x),
                -(interpolated.y),
                -(interpolated.z)
        );

        Deque<Tuple<SwingHistory, SwingHistory>> points = entity.getTrailHistory();
        if (points.size() < 2) return;

        poseStack.pushPose();

        Vec3 last = null;
        float alphaStep = 1.0f / points.size();
        int i = 0;

        for (Tuple<SwingHistory, SwingHistory> point : points) {
            float alpha = 1.0f - (i * alphaStep);
            Vec3 pos = point.getA().position();

            if (last != null && !last.equals(pos)) {
                float length = (float) pos.distanceTo(last);
                //addQuad(consumer, poseStack, pos, point.yaw(), point.pitch(), point.roll(), 0.5f, length, alpha);
                addQuadOld(consumer, poseStack, last, pos, alpha);
            }
            last = pos;
            i++;
        }

        poseStack.popPose();
    }

    private void renderTrailIGuess(FlyingWeaponEntity entity,
                             PoseStack poseStack,
                             float partialticks,
                             MultiBufferSource buffer) {
        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(FootworkRenderTypes.white));
        Vec3 interpolated = entity.getPosition(partialticks); // same as what's applied by default
        poseStack.translate(
                -(interpolated.x),
                -(interpolated.y),
                -(interpolated.z)
        );

        Deque<Tuple<SwingHistory, SwingHistory>> points = entity.getTrailHistory();
        if (points.size() < 2){
            poseStack.popPose();
            return;
        }

        Tuple<SwingHistory,SwingHistory> last = null;
        float alphaStep = 1.0f / points.size();
        int i = 0;

        for (Tuple<SwingHistory, SwingHistory> point : points) {
            float alpha = (1.0f - (i * alphaStep))/2;

            if (last != null && !last.equals(point)) {
                someKindaQuad(consumer, poseStack, last.getB().position(),last.getA().position(),point.getA().position(),point.getB().position(), alpha);
            }
            last = point;
            i++;
        }

        poseStack.popPose();
    }

    private void someKindaQuad(VertexConsumer consumer, PoseStack poseStack, Vec3 from1, Vec3 from2, Vec3 to1, Vec3 to2, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vector3f p1 = from1.toVector3f(), p2=from2.toVector3f(), p3=to1.toVector3f(), p4=to2.toVector3f();

        float r = 0.6f, g = 0.8f, b = 1.0f;
        int light = 15728880;

        consumer.vertex(matrix, p1.x(), p1.y(), p1.z()).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
        consumer.vertex(matrix, p2.x(), p2.y(), p2.z()).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
        consumer.vertex(matrix, p3.x(), p3.y(), p3.z()).color(r, g, b, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
        consumer.vertex(matrix, p4.x(), p4.y(), p4.z()).color(r, g, b, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
    }

    private void addQuadOld(VertexConsumer consumer, PoseStack poseStack, Vec3 from, Vec3 to, float alpha) {
        float width = 0.4f;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        Vector3f dir = new Vector3f((float) (to.x - from.x), (float) (to.y - from.y), (float) (to.z - from.z));
        dir.normalize();

        Vector3f side = new Vector3f(-dir.z(), 0, dir.x());
        side.normalize();
        side.mul(width);

        Vector3f p1 = new Vector3f((float) from.x + side.x(), (float) from.y, (float) from.z + side.z());
        Vector3f p2 = new Vector3f((float) from.x - side.x(), (float) from.y, (float) from.z - side.z());
        Vector3f p3 = new Vector3f((float) to.x - side.x(), (float) to.y, (float) to.z - side.z());
        Vector3f p4 = new Vector3f((float) to.x + side.x(), (float) to.y, (float) to.z + side.z());

        float r = 0.6f, g = 0.8f, b = 1.0f;
        int light = 15728880;

        Vector3f normal = NORMAL; // Flat upward normal

        consumer.vertex(matrix, p1.x(), p1.y(), p1.z()).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
        consumer.vertex(matrix, p2.x(), p2.y(), p2.z()).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
        consumer.vertex(matrix, p3.x(), p3.y(), p3.z()).color(r, g, b, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
        consumer.vertex(matrix, p4.x(), p4.y(), p4.z()).color(r, g, b, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FlyingWeaponEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS; // Not needed, since we're rendering an ItemStack
    }

    public class AlphaMultiBufferSource implements MultiBufferSource {
        private final MultiBufferSource original;
        private final float alpha;

        public AlphaMultiBufferSource(MultiBufferSource original, float alpha) {
            this.original = original;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            return new AlphaVertexConsumer(original.getBuffer(type), alpha);
        }
    }

    public class AlphaVertexConsumer implements VertexConsumer {
        private final VertexConsumer base;
        private final float alpha;

        public AlphaVertexConsumer(VertexConsumer base, float alpha) {
            this.base = base;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            // Override with custom alpha
            return base.color(r, g, b, (int) (alpha * 255));
        }

        // Delegate everything else
        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return base.vertex(x, y, z);
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return base.uv(u, v);
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return base.overlayCoords(u, v);
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return base.uv2(u, v);
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return base.normal(x, y, z);
        }

        @Override
        public void endVertex() {
            base.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            base.defaultColor(r, g, b, a);
        }

        @Override
        public void unsetDefaultColor() {
            base.unsetDefaultColor();
        }
    }


}
