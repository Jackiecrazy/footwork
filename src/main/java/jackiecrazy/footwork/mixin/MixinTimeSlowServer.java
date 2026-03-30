package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.capability.action.AttachAction;
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
        int tickResult = TimeSlowData.getCap(ent).tickDown(ent.tickCount);
        if (ent instanceof Player){
            ent.tick();
            ActionData.getCap(ent).update();
            return;
        }
        while(tickResult>=0){
            ent.tick();
            ActionData.getCap(ent).update();
            tickResult--;
        }
    }
}