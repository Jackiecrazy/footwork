package jackiecrazy.footwork.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import jackiecrazy.footwork.entity.FlyingWeaponEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WeaponEntityRenderer extends EntityRenderer<FlyingWeaponEntity> {
    private final ItemRenderer itemRenderer;

    public WeaponEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FlyingWeaponEntity entity, float yaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        ItemStack stack = entity.getHeldItem();
        if (!stack.isEmpty()) {
            poseStack.pushPose();

            // Position and rotate as needed
            // am I not supposed to lerp? Let's try.
            float lerpYRot = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
            float lerpXRot = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
            float lerpZRot = Mth.rotLerp(partialTicks, entity.rollO, entity.getRoll());
            //float lerpDisplacement = Mth.rotLerp(partialTicks, entity.displacementO, entity.getDisplacementForRender());
            //poseStack.mulPose(Axis.ZP.rotationDegrees(-45));//rotate it up, because GL does stuff backwards
            //poseStack.mulPose(Axis.YP.rotationDegrees(entity.tickCount*10));
            poseStack.mulPose(Axis.YP.rotationDegrees(-lerpYRot)); // Yaw
            poseStack.mulPose(Axis.XP.rotationDegrees(lerpXRot));  // Pitch, +angle to point the sword
            poseStack.mulPose(Axis.ZP.rotationDegrees(lerpZRot));  // Roll
            poseStack.translate(0, 0, -0.8);//adjust weapon offset so the tip is roughly at the entity
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));  // Roll adjustment
            poseStack.mulPose(Axis.XP.rotationDegrees(100));//this rotates a standard iron sword perfectly horizontal

            // Scale and render
            float scale = (float) Math.max(entity.attackRange/3, 0.4);
            poseStack.scale(1f, scale, scale);

            this.itemRenderer.renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight,
                    OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);
            poseStack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FlyingWeaponEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS; // Not needed, since we're rendering an ItemStack
    }
}
