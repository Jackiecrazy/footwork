package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.Footwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public class aaaaaaaaaaa {
    @Inject(method = "remove", locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "HEAD"))
    private void store(Entity.RemovalReason p_146834_, CallbackInfo ci) {
        if((Object)this instanceof ServerPlayer){
            Footwork.LOGGER.fatal("attempted to remove the player. This is very very bad and you should never do this!");
            try{
                throw new RuntimeException("removing the player because "+p_146834_);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
