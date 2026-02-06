package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class ResetHitTimerAction extends Action {
    private Argument<Entity> entity = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionSetWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        if (entity.resolve(wrapper, parent, performer, target) instanceof LivingEntity le) {
            le.hurtDuration = le.hurtTime = 0;
        }
        return 0;
    }
}
