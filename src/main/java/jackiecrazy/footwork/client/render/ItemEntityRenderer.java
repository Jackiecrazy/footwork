package jackiecrazy.footwork.client.render;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import jackiecrazy.footwork.entity.flyingweapon.FlyingItemEntity;
import jackiecrazy.footwork.entity.flyingweapon.FlyingWeaponEffect;
import jackiecrazy.footwork.entity.flyingweapon.SwingHistory;
import jackiecrazy.footwork.move.motionframe.RenderItemGroup;
import jackiecrazy.footwork.move.motionframe.ItemNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Deque;

public class ItemEntityRenderer extends EntityRenderer<FlyingItemEntity> {
    public static final Vector3f NORMAL = new Vector3f(0, 1, 0);
    public static MultiBufferSource TEMP_OVERRIDE = null;
    protected final ItemRenderer itemRenderer;

    public ItemEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FlyingItemEntity entity,
                       float yaw,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight) {
        if (buffer instanceof OutlineBufferSource obs) {
            obs.setColor((int) (Math.sin(Math.toRadians(entity.tickCount)) * 128) + 128, 256 - (int) (Math.sin(Math.toRadians(entity.tickCount)) * 128) + 128, (int) (Math.cos(Math.toRadians(entity.tickCount)) * 128) + 128, 255);
        }
        RenderItemGroup stack = entity.getCosmeticItem();
        //if (!stack.isEmpty()) {

        if (entity.hasEffect(FlyingWeaponEffect.WEAPON))
            renderFlyingWeapon(entity, partialTicks, poseStack, stack);
        if (entity.hasEffect(FlyingWeaponEffect.AFTERIMAGE))
            renderAfterimages(entity, partialTicks, poseStack, buffer, packedLight, stack);
        if (entity.hasEffect(FlyingWeaponEffect.BIG_SHADOW))
            renderBigShadowWeapon(entity, partialTicks, poseStack, buffer, 0xF000F0, stack);
        //}
        if (entity.hasEffect(FlyingWeaponEffect.TRAIL))
            renderTrail(entity, poseStack, partialTicks, buffer);
    }

    protected void renderFlyingWeapon(FlyingItemEntity entity,
                                      float partialTicks,
                                      PoseStack poseStack,
                                      RenderItemGroup group) {
        poseStack.pushPose();
        for(ItemNode n:group.nodes()) {
            ItemStack stack = n.stack();
            poseStack.pushPose();
//        if (!entity.shouldRender(FlyingWeaponEffect.BIG_SHADOW)) {
//
//            Vec3 interpolated = entity.getPosition(partialTicks); // same as what's applied by default
//            poseStack.translation(
//                    -(interpolated.x),
//                    -(interpolated.y),
//                    -(interpolated.z)
//            );
//            interpolated = entity.getTrailHistory().getFirst().getA().position();
//            poseStack.translation(
//                    (interpolated.x),
//                    (interpolated.y),
//                    (interpolated.z)
//            );
//        }
            // rotation
            Quaternionf quat = entity.rollO.slerp(entity.getRoll(), partialTicks, new Quaternionf());
            poseStack.mulPose(quat);
            poseStack.translate(0, 0, -0.4);//adjust weapon offset so it's at the middle
            //poseStack.translation(0, 0, -0.8);//adjust weapon offset so the tip is roughly at the entity
            // Scale and render
            float scale = 1;// (float) Math.max(Mth.lerp(partialTicks, entity.sizeO, entity.getInteractionRange()) / 3, 0.4);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
            poseStack.mulPose(Axis.XP.rotationDegrees(99));//this rotates a standard iron sword perfectly horizontal

            //actual weapon
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            final int packedlight = 0xf000f0;
            renderStackProperly(n, poseStack, bufferSource, packedlight, itemRenderer.getModel(stack, entity.level(), null, 0), entity.flipClientRender());
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    protected void renderBigShadowWeapon(FlyingItemEntity entity,
                                         float partialTicks,
                                         PoseStack poseStack,
                                         MultiBufferSource buffer,
                                         int packedLight,
                                         RenderItemGroup stack) {
        poseStack.pushPose();
        SwingHistory from;
        SwingHistory to = null;
        //cancel all natural entity offsets first
        Vec3 interpolated = entity.getPosition(partialTicks); // same as what's applied by default
        poseStack.translate(-(interpolated.x), -(interpolated.y), -(interpolated.z));
        int skip = 0;
        float lerpRenderLag = (Mth.lerp(partialTicks, entity.renderLagO, entity.renderLag) * FlyingItemEntity.CLIENT_SMOOTHING_SUBTICKS);
        int lerpAlpha = (int) (Mth.lerp(partialTicks, entity.alphaO, entity.alpha));
        for (Tuple<SwingHistory, SwingHistory> sh : entity.getTrailHistory()) {
            from = to;
            to = sh.getB();
            skip += 1;
            if (from != null && to != null && skip >= lerpRenderLag) {
                poseStack.pushPose();
                float moddedPartialTicks = (partialTicks % (1f / FlyingItemEntity.CLIENT_SMOOTHING_SUBTICKS)) * FlyingItemEntity.CLIENT_SMOOTHING_SUBTICKS;
                final float attackRange = Mth.lerp(partialTicks, entity.sizeO, entity.getInteractionRange());
                float scale = (float) Math.max(attackRange, 0.4);
                Vec3 interpolate = from.position().lerp(to.position(), moddedPartialTicks);
                poseStack.translate(interpolate.x, interpolate.y, interpolate.z);
                // Position and rotation as needed
                Quaternionf quat = entity.rollO.slerp(entity.getRoll(), partialTicks, new Quaternionf());
                poseStack.mulPose(quat);
                poseStack.scale(scale, scale, scale);//should scale here, right?
                poseStack.translate(0, 0, 0.2);//aligning pommel to the best of my ability
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
                poseStack.mulPose(Axis.XP.rotationDegrees(99));//this rotates a standard iron sword perfectly horizontal
                poseStack.translate(0, -0.25, 0);//trying to put the weapon on the same axis

                //poseStack.translation(0.425, -0.45, 0);//Obsolete. trying to put the weapon on the same axis

                //afterimages
//                    MultiBufferSource bufferWithAlpha = new AlphaMultiBufferSource(buffer, alpha);
//                    itemRenderer.render(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferWithAlpha, packedLight, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null, 0));

                renderShadowWeapon(stack, poseStack, buffer, lerpAlpha, entity.flipClientRender());

                poseStack.popPose();
                break;
            }
        }
        poseStack.popPose();
    }

    protected void renderAfterimages(FlyingItemEntity entity,
                                     float partialTicks,
                                     PoseStack poseStack,
                                     MultiBufferSource buffer,
                                     int packedLight,
                                     RenderItemGroup stack) {
        poseStack.pushPose();
        float alpha = 1f;
        SwingHistory sh0;
        SwingHistory sh1 = null;
        //cancel all natural entity offsets first
        Vec3 interpolated = entity.getPosition(partialTicks); // same as what's applied by default
        poseStack.translate(-(interpolated.x), -(interpolated.y), -(interpolated.z));
        int skip = 0;
        for (Tuple<SwingHistory, SwingHistory> sh : entity.getTrailHistory()) {
            sh0 = sh1;
            //if the big shadow is rendered, the afterimage is kept where the weapon is, otherwise it scales out
            sh1 = sh.getB();//entity.shouldRender(FlyingWeaponEffect.BIG_SHADOW) ? sh.getB() : sh.getA();
            skip += 1;
            final int threshold = FlyingItemEntity.CLIENT_SMOOTHING_SUBTICKS;
            if (sh0 != null && skip >= threshold) {
                skip %= threshold;
                alpha *= 0.7f;
                renderAfterimage(entity, partialTicks, poseStack, buffer, packedLight, stack, sh0, sh1, alpha);
            }
        }
        poseStack.popPose();
    }

    protected void renderShadowWeapon(RenderItemGroup group, PoseStack poseStack, MultiBufferSource bufferSource, int alpha,
                                      boolean left) {
        poseStack.pushPose();
        for(ItemNode n:group.nodes()) {
            ItemStack stack=n.stack();
            poseStack.pushPose();
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
            ResourceLocation a = model.isCustomRenderer() ? FootworkRenderTypes.white : model.getParticleIcon(ModelData.EMPTY).atlasLocation();
//        itemRenderer.render(
//                stack,
//                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
//                false,
//                poseStack,
//                type -> new CustomVertexConsumer(bufferSource.getBuffer(RenderType.entityTranslucentCull(a)),0,0,0, alpha),
//                0xf00f0,
//                OverlayTexture.NO_OVERLAY,
//                model  // or null
//        );
            final int packedlight = 0xf00f0;

            //create the shadow MBS
            TEMP_OVERRIDE = type -> {
                // Let the vanilla glint passes use their normal consumer (so glint still shows)
                if (type == RenderType.entityGlint() || type == RenderType.entityGlintDirect()) {
                    return bufferSource.getBuffer(type);
                }
                RenderType rt = type;
                if (model.isCustomRenderer() || stack.getItem() instanceof BlockItem)
                    rt = RenderType.entityTranslucentCull(a);
                // Otherwise use the requested RenderType but wrap it to tint/alpha it
                return new CustomVertexConsumer(bufferSource.getBuffer(rt), 0, 0, 0, alpha / 256f);
            };

            renderStackProperly(n, poseStack, TEMP_OVERRIDE, packedlight, model, left);
            TEMP_OVERRIDE = null;
            poseStack.popPose();
        }
        poseStack.popPose();
    }
    private void renderStackProperly(ItemNode n,
                                     PoseStack poseStack,
                                     MultiBufferSource bf,
                                     int packedlight,
                                     BakedModel model, boolean left) {
        ItemStack stack =n.stack();
        if (stack.isEmpty()) {
            poseStack.pushPose();
            int flip = left ? -1 : 1;
            //poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            poseStack.translate(n.translation().x, n.translation().y, n.translation().z);
            poseStack.translate(flip * 0.37, 0.3, -0.05);
            PlayerRenderer playerrenderer = (PlayerRenderer) this.entityRenderDispatcher.<AbstractClientPlayer>getRenderer(Minecraft.getInstance().player);
            if (left)
                playerrenderer.renderLeftHand(poseStack, bf, packedlight, Minecraft.getInstance().player);
            else playerrenderer.renderRightHand(poseStack, bf, packedlight, Minecraft.getInstance().player);
            poseStack.popPose();
        } else if (stack.getItem() instanceof BlockItem blockItem && !(stack.getItem() instanceof ItemNameBlockItem)) {
            Block block = blockItem.getBlock();
            BlockState base = block.defaultBlockState();
            BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();

            // Push transform for positioning
            if (block instanceof DoorBlock) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees((float) n.rotation().x));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) n.rotation().y));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) n.rotation().z));
                poseStack.translate(n.translation().x, n.translation().y, n.translation().z);
                poseStack.translate(-0.5, 0, -0.5);
                BlockState lower = base;
                BlockState upper;
                try {
                    lower = base.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
                    upper = base.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
                } catch (IllegalArgumentException e) {
                    // If properties aren't present for some reason, fallback to default halves
                    lower = base;
                    upper = base;
                }

                // render lower at origin
                brd.renderSingleBlock(lower, poseStack, bf, packedlight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY,//itemRenderer.getModel(stack, entity.level(), null, packedlight).getModelData(),
                                      null);

                // render upper one block above
                poseStack.translate(0.0, 1.0, 0.0);
                brd.renderSingleBlock(upper, poseStack, bf, packedlight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY,//itemRenderer.getModel(stack, entity.level(), null, packedlight).getModelData(),
                                      null);
                poseStack.popPose();
            } else {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees((float) n.rotation().x));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) n.rotation().y));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) n.rotation().z));
                poseStack.translate(n.translation().x, n.translation().y, n.translation().z);
                poseStack.translate(-0.5, 0, -0.5);

                // Use the block renderer
                brd.renderSingleBlock(base, poseStack, bf, packedlight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY,//itemRenderer.getModel(stack, entity.level(), null, packedlight).getModelData(),
                                      null);
                //is this the right order?
                poseStack.popPose();
            }
        } else {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees((float) n.rotation().x));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) n.rotation().y));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) n.rotation().z));
            ItemDisplayContext ctx = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            //GL11.glFrontFace(GL11.GL_CW);
            if (left) {
                poseStack.scale(-1, 1, 1);
            }
            poseStack.translate(n.translation().x, n.translation().y, n.translation().z);
            itemRenderer.render(stack, ctx, false, poseStack, bf, packedlight, OverlayTexture.NO_OVERLAY, model  // or null
            );
            //GL11.glFrontFace(GL11.GL_CCW);
            poseStack.popPose();
        }
    }

    protected void renderAfterimage(FlyingItemEntity entity,
                                    float partialTicks,
                                    PoseStack poseStack,
                                    MultiBufferSource buffer,
                                    int packedLight,
                                    RenderItemGroup group,
                                    SwingHistory from,
                                    SwingHistory to,
                                    float alpha) {
        poseStack.pushPose();
        for(ItemNode n:group.nodes()) {
            ItemStack stack = n.stack();
            poseStack.pushPose();

            Vec3 interpolate = from.position().lerp(to.position(), partialTicks);
            poseStack.translate(interpolate.x, interpolate.y, interpolate.z);
            // Position and rotation as needed
            Quaternionf quat = from.orientation().slerp(to.orientation(), partialTicks, new Quaternionf());
            poseStack.mulPose(quat);
            poseStack.translate(0, 0, -0.4);//adjust weapon offset so the tip is roughly at the entity
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
            poseStack.mulPose(Axis.XP.rotationDegrees(99));//this rotates a standard iron sword perfectly horizontal

            // Scale and render
            float scale = (float) 1;//Math.max(Mth.lerp(partialTicks, entity.sizeO, entity.getInteractionRange()) / 3, 0.4);
            poseStack.scale(scale, scale, scale);

            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
            ResourceLocation a = model.isCustomRenderer() ? FootworkRenderTypes.white : model.getParticleIcon(ModelData.EMPTY).atlasLocation();
            //afterimages
            TEMP_OVERRIDE = type -> {
                // Let the vanilla glint passes use their normal consumer (so glint still shows)
                if (type == RenderType.entityGlint() || type == RenderType.entityGlintDirect()) {
                    return buffer.getBuffer(type);
                }
                RenderType rt = type;
                if (model.isCustomRenderer() || stack.getItem() instanceof BlockItem)
                    rt = RenderType.entityTranslucentCull(a);
                // Otherwise use the requested RenderType but wrap it to tint/alpha it
                return new CustomVertexConsumer(buffer.getBuffer(rt), alpha);
            };
            //fixme enchanting causes afterimages to not show up
            renderStackProperly(n, poseStack, TEMP_OVERRIDE, packedLight, model, entity.flipClientRender());
            TEMP_OVERRIDE = null;
            //itemRenderer.render(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferWithAlpha, packedLight, OverlayTexture.NO_OVERLAY, model);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    protected void renderTrail(FlyingItemEntity entity,
                               PoseStack poseStack,
                               float partialticks,
                               MultiBufferSource buffer) {
        //fixme it's coming out as a step pattern...?
        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(FootworkRenderTypes.white));
        Vec3 interpolated = entity.getPosition(partialticks); // same as what's applied by default
        poseStack.translate(-(interpolated.x), -(interpolated.y), -(interpolated.z));

        Deque<Tuple<SwingHistory, SwingHistory>> points = entity.getTrailHistory();
        if (points.size() < 2) {
            poseStack.popPose();
            return;
        }

        Tuple<SwingHistory, SwingHistory> last = null;
        float alphaStep = 1.0f / points.size();
        int i = 0;

        for (Tuple<SwingHistory, SwingHistory> point : points) {
            i++;
            //if(i%FlyingItemEntity.!=0)continue;
            float alpha = (1.0f - (i * alphaStep)) / 2;

            if (last != null && !last.equals(point) && point.getA().corporeal()) {
                drawQuad(consumer, poseStack, last.getB().position(), last.getA().position(), point.getA().position(), point.getB().position(), point.getA().color(), alpha);
            }
            last = point;
        }

        poseStack.popPose();
    }

    protected void drawQuad(VertexConsumer consumer,
                            PoseStack poseStack,
                            Vec3 from1,
                            Vec3 from2,
                            Vec3 to1,
                            Vec3 to2,
                            Color c,
                            float alpha) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vector3f p1 = from1.toVector3f(), p2 = from2.toVector3f(), p3 = to1.toVector3f(), p4 = to2.toVector3f();

        float r = c.getRed()/255f, g = c.getGreen()/255f, b = c.getBlue()/255f;
        //float r = 0.6f, g = 0.8f, b = 1.0f;
        int light = 15728880;

        consumer.vertex(matrix, p1.x(), p1.y(), p1.z()).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
        consumer.vertex(matrix, p2.x(), p2.y(), p2.z()).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
        consumer.vertex(matrix, p3.x(), p3.y(), p3.z()).color(r, g, b, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
        consumer.vertex(matrix, p4.x(), p4.y(), p4.z()).color(r, g, b, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, NORMAL.x(), NORMAL.y(), NORMAL.z()).endVertex();
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FlyingItemEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS; // Not needed, since we're rendering an ItemStack
    }

    public static class CustomVertexConsumer implements VertexConsumer {
        protected final VertexConsumer base;
        protected final float alpha;
        protected final float re, gr, bl;

        public CustomVertexConsumer(VertexConsumer base, float r, float g, float b, float alpha) {
            re = r;
            gr = g;
            bl = b;
            this.base = base;
            this.alpha = alpha;
        }

        public CustomVertexConsumer(VertexConsumer base, float alpha) {
            this(base, -1, -1, -1, alpha);
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            if (re >= 0) r = (int) (re * 255);
            if (gr >= 0) g = (int) (gr * 255);
            if (bl >= 0) b = (int) (bl * 255);
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
