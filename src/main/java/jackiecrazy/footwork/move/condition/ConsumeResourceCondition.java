package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.ResourceEnums;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.number.NumberArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class ConsumeResourceCondition extends Condition {
    private Argument<Entity> reference_point = CasterEntityArgument.INSTANCE;
    private ResourceEnums.ResourceFormat format = ResourceEnums.ResourceFormat.NUMBER;
    private ResourceEnums.TYPE resource;
    private NumberArgument amount;
    private ResourceEnums.ResourceOperation operation;

    @Override
    public Boolean resolve(TimerActionsWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        return reference_point.resolve(wrapper, parent, performer, target) instanceof LivingEntity le && operation.apply(le, resource, amount.resolve(wrapper, parent, performer, target), format);
    }
}
