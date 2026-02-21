package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.ResourceEnums;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.number.NumberArgument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlterResourceAction extends Action {
    private Argument<Entity> reference_point = CasterEntityArgument.INSTANCE;
    private ResourceEnums.ResourceFormat format = ResourceEnums.ResourceFormat.NUMBER;
    private ResourceEnums.TYPE resource;
    private NumberArgument amount;
    private ResourceEnums.ResourceOperation operation;

    @Override
    public int perform(ActionContext argumentContext) {
        if (reference_point.resolve(argumentContext) instanceof LivingEntity le)
            operation.apply(le, resource, amount.resolve(argumentContext), format);
        return 0;
    }
}
