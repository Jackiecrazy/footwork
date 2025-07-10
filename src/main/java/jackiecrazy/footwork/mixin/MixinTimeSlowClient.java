package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientLevel.class)
public abstract class MixinTimeSlowClient {
    @Redirect(method = "tickNonPassenger", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void slow(Entity ent) {
        if (ent instanceof Player){
            ent.tick();
            return;
        }
        int tickResult = TimeSlowData.getCap(ent).tickDown(ent.tickCount);
        while(tickResult>=0){
            ent.tick();
            tickResult--;
        }
    }
}