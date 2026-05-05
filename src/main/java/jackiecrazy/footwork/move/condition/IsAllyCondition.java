package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.utils.TargetingUtils;
import net.minecraft.world.entity.Entity;

public class IsAllyCondition extends Condition {
    private Argument<Entity> reference = CasterEntityArgument.INSTANCE;
    private Argument<Entity> subject = TargetEntityArgument.INSTANCE;
    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        Entity ref= reference.resolve(argumentContext);
        Entity oth= subject.resolve(argumentContext);
        return TargetingUtils.isAlly(ref, oth);
    }
}
