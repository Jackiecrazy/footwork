package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.client.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinParalysis {

    @Shadow private double accumulatedDX;

    @Shadow private double accumulatedDY;

    @Redirect(method = "grabMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private void paralyze(Minecraft mc, Screen s) {
        if (!ClientEvents.paralysis)
            mc.setScreen(s);
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void noturn(CallbackInfo ci) {
        if (ClientEvents.paralysis) {
            accumulatedDX = 0.0D;
            accumulatedDY = 0.0D;
            ci.cancel();
        }
    }
}
