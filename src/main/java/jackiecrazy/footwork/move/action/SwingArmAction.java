package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SwingArmAction extends Action {
    private InteractionHand hand;
    private Argument<Entity> swinger = CasterEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        if (swinger.resolve(actionContext) instanceof LivingEntity e) {
            e.swing(hand, true);
            e.level().broadcastEntityEvent(e, (byte)4);
        }
        return 0;
    }
}
