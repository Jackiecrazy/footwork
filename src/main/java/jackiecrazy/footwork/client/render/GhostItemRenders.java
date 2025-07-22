package jackiecrazy.footwork.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GhostItemRenders {
    public static void renderGhostItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, float alpha, int light) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        // Use the default baked model for the item
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);

        // Use a translucent RenderType so alpha can work
        RenderType translucent = RenderType.entityTranslucentCull(
                //model.getRenderTypes(stack, true).get(0) //FABULOUS?
                itemRenderer.getItemModelShaper().getItemModel(stack.getItem()).getParticleIcon().atlasLocation());

        // Wrap the VertexConsumer to inject a custom alpha value
        VertexConsumer baseConsumer = buffer.getBuffer(translucent);
        VertexConsumer alphaConsumer = new AlphaOverrideConsumer(baseConsumer, alpha);

        itemRenderer.render(stack, ItemDisplayContext.FIXED, false, poseStack, buffer, light, OverlayTexture.NO_OVERLAY, model);
    }

    public static void renderGlintOnly(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, float scale, int light) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        BakedModel model = itemRenderer.getModel(stack, null, null, 0);

        // Use built-in FOIL RenderType for glint
        RenderType glintType = RenderType.entityGlint();

        // Pose transform for ghost positioning
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        // Get glint buffer
        VertexConsumer glintConsumer = buffer.getBuffer(glintType);

        // Manually render only the glint
        itemRenderer.renderModelLists(model, stack, light, OverlayTexture.NO_OVERLAY, poseStack, glintConsumer);

        poseStack.popPose();
    }

    /**
     * A wrapper that forces all vertex alpha values to the given amount.
     */
    private static class AlphaOverrideConsumer implements VertexConsumer {
        private final VertexConsumer parent;
        private final float forcedAlpha;

        public AlphaOverrideConsumer(VertexConsumer parent, float forcedAlpha) {
            this.parent = parent;
            this.forcedAlpha = forcedAlpha;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return parent.vertex(x, y, z);
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            return parent.color(r, g, b, (int) (forcedAlpha * 255f));
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return parent.uv(u, v);
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return parent.overlayCoords(u, v);
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return parent.uv2(u, v);
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return parent.normal(x, y, z);
        }

        @Override
        public void endVertex() {
            parent.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            parent.defaultColor(r, g, b, a);
        }

        @Override
        public void unsetDefaultColor() {
            parent.unsetDefaultColor();
        }
    }
}
