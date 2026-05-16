package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class WetMixin {


    @SuppressWarnings("all")
    @Inject(method = "isInWater", at=@At("RETURN"), cancellable = true)
    private void aaa(CallbackInfoReturnable<Boolean> cir){
        if((Object)this instanceof LivingEntity e && e.hasEffect(FootworkEffects.WET.get())){
            cir.setReturnValue(true);
        }
    }
}
