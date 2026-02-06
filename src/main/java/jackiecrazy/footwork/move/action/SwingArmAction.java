package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class SwingArmAction extends Action {
    private InteractionHand hand;
    private Argument<Entity> swinger = CasterEntityArgument.INSTANCE;

    @Override
    public int perform(ActionSetWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        if (swinger.resolve(wrapper, parent, performer, target) instanceof LivingEntity e) {
            e.swing(hand, true);
            e.level().broadcastEntityEvent(e, (byte)4);
        }
        return 0;
    }
}
