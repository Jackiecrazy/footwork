package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinTimeSlow {
    @Shadow
    public abstract void tick();

    @Redirect(method = "rideTick", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void slow(Entity ent) {
        int tickResult = TimeSlowData.getCap(ent).tickDown(ent.tickCount);
        //players just get a modification to their fall speed
        if (ent instanceof Player){
            tick();
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