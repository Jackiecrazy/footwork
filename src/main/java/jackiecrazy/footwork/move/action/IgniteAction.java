package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.world.entity.Entity;

public class IgniteAction extends Action {
    private Argument<Double> time;
    private Argument<Entity> target = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        target.resolve(actionContext).setRemainingFireTicks(time.resolve(actionContext).intValue());
        return 0;
    }
}
