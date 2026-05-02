package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PinAction extends Action {
    private Argument<Double> duration;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        Entity e = recipient.resolve(actionContext);
        if (e instanceof LivingEntity le) {
            CombatData.getCap(le).pin(duration.resolve(actionContext).intValue());
        }
        return 0;
    }
}
