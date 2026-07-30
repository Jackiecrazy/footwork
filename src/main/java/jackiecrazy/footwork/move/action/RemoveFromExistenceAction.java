package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class RemoveFromExistenceAction extends Action {
    private Argument<Entity> entity = TargetEntityArgument.INSTANCE;
    private Entity.RemovalReason reason = Entity.RemovalReason.KILLED;

    @Override
    public int perform(ActionContext actionContext) {
        final Entity rip = entity.resolve(actionContext);
        if (rip instanceof Player) {
            Footwork.LOGGER.fatal("attempted to remove the player. This is very very bad and you should never use actions to do this! This action chain will now stop to prevent further problems.");
            return -1;
        }
        if (rip != null)
            rip.remove(reason);
        return 0;
    }
}
