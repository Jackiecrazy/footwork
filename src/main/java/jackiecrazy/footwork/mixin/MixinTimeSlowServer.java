package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class MixinTimeSlowServer {
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