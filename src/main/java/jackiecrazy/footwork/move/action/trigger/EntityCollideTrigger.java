package jackiecrazy.footwork.move.action.trigger;

import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.utils.MovementUtils;

public class EntityCollideTrigger extends Trigger{
    @Override
    public boolean canRun(ActionContext actionContext) {
        MovementUtils.collidingEntity(actionContext.performer());
        return super.canRun(actionContext);
    }
}
