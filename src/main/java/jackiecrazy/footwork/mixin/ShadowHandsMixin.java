package jackiecrazy.footwork.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import jackiecrazy.footwork.client.render.ItemEntityRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class ShadowHandsMixin {
    private AbstractClientPlayer acp;
    @Inject(method = "renderHand",
            at = @At(value = "HEAD"))
    private void mark(PoseStack p_117776_,
                      MultiBufferSource p_117777_,
                      int p_117778_,
                      AbstractClientPlayer p,
                      ModelPart p_117780_,
                      ModelPart p_117781_,
                      CallbackInfo ci) {
        acp=p;
    }

    @Redirect(method = "renderHand", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer slow(MultiBufferSource instance, RenderType renderType) {
        if(ItemEntityRenderer.TEMP_OVERRIDE!=null&&acp!=null){
            return ItemEntityRenderer.TEMP_OVERRIDE.getBuffer(RenderType.entityTranslucentCull(acp.getSkinTextureLocation()));
        }
        return instance.getBuffer(renderType);
    }
}
