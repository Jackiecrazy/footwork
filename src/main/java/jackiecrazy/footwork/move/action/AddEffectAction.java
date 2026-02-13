package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class AddEffectAction extends Action {
    private Argument<ResourceLocation> effect;
    private transient MobEffect me;
    private Argument<Double> potency, duration;
    private Argument<Entity> recipient= TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        if (me == null)
            me = ForgeRegistries.MOB_EFFECTS.getValue(effect.resolve(actionContext));
        if (me != null && recipient.resolve(actionContext) instanceof LivingEntity e) {
            e.addEffect(new MobEffectInstance(me, duration.resolve(actionContext).intValue(), potency.resolve(actionContext).intValue()));
        }
        return 0;
    }
}
