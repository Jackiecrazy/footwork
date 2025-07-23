package jackiecrazy.footwork.client.render;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import jackiecrazy.footwork.entity.flyingweapon.FlyingWeaponEntity;
import jackiecrazy.footwork.entity.flyingweapon.SwingHistory;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WeaponEntityRenderer extends EntityRenderer<FlyingWeaponEntity> {
    private final ItemRenderer itemRenderer;

    public WeaponEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FlyingWeaponEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
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
            poseStack.mulPose(Axis.XP.rotationDegrees(lerpXRot));  // Pitch, +angle to point the sword
            poseStack.mulPose(Axis.ZP.rotationDegrees(lerpZRot));  // Roll
            poseStack.translate(0, 0, -0.8);//adjust weapon offset so the tip is roughly at the entity
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
            poseStack.mulPose(Axis.XP.rotationDegrees(100));//this rotates a standard iron sword perfectly horizontal
            // Scale and render
            float scale = (float) Math.max(entity.attackRange / 3, 0.4);
            poseStack.scale(1f, scale, scale);

            //actual weapon
            this.itemRenderer.renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);
//            poseStack.translate(0,1,0);
//            MultiBufferSource bufferWithAlpha = new AlphaMultiBufferSource(buffer, 0.5f);
//            itemRenderer.render(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferWithAlpha, packedLight, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null, 0));
            poseStack.popPose();

            //afterimage render//
            poseStack.pushPose();
            float alpha = 1f;
            SwingHistory sh0;
            SwingHistory sh1 = null;
            //cancel all natural entity offsets first
            Vec3 interpolated = entity.getPosition(partialTicks); // same as what's applied by default
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 camPos = camera.getPosition();
            poseStack.translate(
                    -(interpolated.x),
                    -(interpolated.y),
                    -(interpolated.z)
            );
            for (SwingHistory sh : entity.getHistory()) {
                sh0 = sh1;
                sh1 = sh;
                if (sh0 != null && sh1 != null) {
                    alpha *= 0.5f;
                    renderAfterimage(entity, partialTicks, poseStack, buffer, packedLight, stack, sh0, sh1, alpha);
                }
            }
            poseStack.popPose();
        }
    }

    private void renderAfterimage(FlyingWeaponEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack stack, SwingHistory from, SwingHistory to, float alpha) {
        poseStack.pushPose();

        Vec3 interpolate = from.position().lerp(to.position(), partialTicks);
        poseStack.translate(interpolate.x, interpolate.y, interpolate.z);
        // Position and rotate as needed
        float lerpYRot = Mth.rotLerp(partialTicks, from.pitch(), to.pitch());
        float lerpXRot = Mth.rotLerp(partialTicks, from.yaw(), to.yaw());
        float lerpZRot = Mth.rotLerp(partialTicks, from.roll(), to.roll());
        //float lerpDisplacement = Mth.rotLerp(partialTicks, entity.displacementO, entity.getDisplacementForRender());
        poseStack.mulPose(Axis.YP.rotationDegrees(-lerpYRot)); // Yaw
        poseStack.mulPose(Axis.XP.rotationDegrees(lerpXRot));  // Pitch, +angle to point the sword
        poseStack.mulPose(Axis.ZP.rotationDegrees(lerpZRot));  // Roll
        poseStack.translate(0, 0, -0.8);//adjust weapon offset so the tip is roughly at the entity
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
        poseStack.mulPose(Axis.XP.rotationDegrees(100));//this rotates a standard iron sword perfectly horizontal

        //TODO move here


        // Scale and render
        float scale = (float) Math.max(entity.attackRange / 3, 0.4);
        poseStack.scale(1f, scale, scale);

        //afterimages
        MultiBufferSource bufferWithAlpha = new AlphaMultiBufferSource(buffer, alpha);
        itemRenderer.render(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferWithAlpha, packedLight, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null, 0));
        poseStack.popPose();
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
