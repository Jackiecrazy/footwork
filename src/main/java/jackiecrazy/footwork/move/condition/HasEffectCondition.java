package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class HasEffectCondition extends Condition {
    private Argument<ResourceLocation> effect;
    private transient MobEffect me;
    private Argument<Double> minimum_potency= FixedNumberArgument.ZERO, minimum_duration= new FixedNumberArgument(-2);
    private Argument<Entity> tested = TargetEntityArgument.INSTANCE;

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        if (me == null)
            me = ForgeRegistries.MOB_EFFECTS.getValue(effect.resolve(argumentContext));
        if (me != null && tested.resolve(argumentContext) instanceof LivingEntity e) {
            MobEffectInstance inst = e.getEffect(me);
            if (inst != null) {
                return inst.getDuration() >= (minimum_duration.resolve(argumentContext)) && inst.getAmplifier() >= minimum_potency.resolve(argumentContext);
            }
        }
        return false;
    }
}
