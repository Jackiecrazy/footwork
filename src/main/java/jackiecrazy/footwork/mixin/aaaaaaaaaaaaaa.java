package jackiecrazy.footwork.mixin;

import jackiecrazy.footwork.Footwork;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VeryBiasedToBottomHeight.class)
public class aaaaaaaaaaaaaa {
    @Shadow
    @Final
    private VerticalAnchor minInclusive;

    @Shadow
    @Final
    private VerticalAnchor maxInclusive;

    @Shadow
    @Final
    private int inner;

    @Inject(method = "sample",
            at = @At(value = "HEAD"))
    private void error(RandomSource p_226311_, WorldGenerationContext p_226312_, CallbackInfoReturnable<Integer> cir) {
        int i = minInclusive.resolveY(p_226312_);
        int j = maxInclusive.resolveY(p_226312_);
        if (j - i - inner + 1 <= 0) {
            try {
                throw new RuntimeException("generating invalid " + p_226312_.toString());
            } catch (RuntimeException e) {
                Footwork.LOGGER.fatal("Please do not generate structures like this.");
                e.printStackTrace();
            }
        }
    }
}
