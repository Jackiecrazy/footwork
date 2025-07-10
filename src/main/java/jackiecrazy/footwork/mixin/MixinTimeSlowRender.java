package jackiecrazy.footwork.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LevelRenderer.class)
public abstract class MixinTimeSlowRender {
    @Shadow
    protected abstract void renderEntity(Entity p_109518_, double p_109519_, double p_109520_, double p_109521_, float p_109522_, PoseStack p_109523_, MultiBufferSource p_109524_);

//    @ModifyArgs(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
//    private void modifyEntityYaw(@NotNull Args args) {
//        Entity entity = args.get(0); // The entity being rendered
//        float oldPartialTicks = args.get(4);
//        float pt = TimeSlowData.getCap(entity).getPartialTick(oldPartialTicks);
//        args.set(4, pt);
//    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    private void slow(LevelRenderer instance, Entity ent, double d0, double d1, double d2, float partialticks, PoseStack s, MultiBufferSource m) {
        if (ent instanceof Player) {
            renderEntity(ent, d0, d1, d2, partialticks, s, m);
            return;
        }
        renderEntity(ent, d0, d1, d2, TimeSlowData.getCap(ent).getPartialTick(partialticks), s, m);
    }
}