package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.ResourceEnums;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.number.NumberArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ConsumeResourceCondition extends Condition {
    private Argument<Entity> reference_point = CasterEntityArgument.INSTANCE;
    private ResourceEnums.ResourceFormat format = ResourceEnums.ResourceFormat.NUMBER;
    private ResourceEnums.TYPE resource;
    private NumberArgument amount;

    public ConsumeResourceCondition() {
    }

    public ConsumeResourceCondition(ResourceEnums.ResourceFormat format,
                                    ResourceEnums.TYPE resource,
                                    NumberArgument amount) {
        this.reference_point = CasterEntityArgument.INSTANCE;
        this.format = format;
        this.resource = resource;
        this.amount = amount;
    }

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        return reference_point.resolve(argumentContext) instanceof LivingEntity le && ResourceEnums.ResourceOperation.CONSUME.apply(le, resource, amount.resolve(argumentContext), format);
    }
}
