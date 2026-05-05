package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class FreezeAction extends Action {
    private Argument<Double> time;
    private Argument<Entity> target = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        final Entity r = target.resolve(actionContext);
        r.setTicksFrozen(time.resolve(actionContext).intValue());
        return 0;
    }
}
