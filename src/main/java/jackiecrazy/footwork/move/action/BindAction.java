package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BindAction extends Action {
    private Argument<Double> duration;
    private InteractionHand hand;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        Entity e = recipient.resolve(actionContext);
        if (e instanceof LivingEntity le) {
            if (hand == null)
                CombatData.getCap(le).bindHands(duration.resolve(actionContext).intValue());
            else
                CombatData.getCap(le).setHandBind(hand, duration.resolve(actionContext).intValue());
        }
        return 0;
    }
}
