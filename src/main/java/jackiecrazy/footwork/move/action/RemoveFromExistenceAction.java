package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import net.minecraft.world.entity.Entity;

public class RemoveFromExistenceAction extends Action {
    private Argument<Entity> entity = TargetEntityArgument.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        entity.resolve(actionContext).remove(Entity.RemovalReason.KILLED);
        return 0;
    }
}
