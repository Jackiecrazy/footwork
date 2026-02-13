package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ResetHitTimerAction extends Action {
    private Argument<Entity> entity = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        if (entity.resolve(actionContext) instanceof LivingEntity le) {
            le.hurtDuration = le.hurtTime = 0;
        }
        return 0;
    }
}
