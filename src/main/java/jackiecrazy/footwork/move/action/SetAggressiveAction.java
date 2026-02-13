package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.condition.Condition;
import net.minecraft.world.entity.Mob;

public class SetAggressiveAction extends Action {
    Condition toggle;

    @Override
    public int perform(ActionContext actionContext) {
        if (actionContext.performer() instanceof Mob e) {
            e.setAggressive(toggle.resolve(actionContext));
        }
        return 0;
    }
}
