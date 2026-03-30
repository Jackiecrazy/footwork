package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class SteveTimeAction extends Action {
    private Argument<Double> speed, duration;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        Entity e = recipient.resolve(actionContext);
        if (e != null) {
            TimeSlowData.getCap(e).alterSpeed(duration.resolve(actionContext).intValue(), speed.resolve(actionContext).intValue());
        }
        return 0;
    }
}
