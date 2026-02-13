package jackiecrazy.footwork.move.action.trigger;

import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.utils.ActionContext;

import java.util.ArrayList;
import java.util.List;

public class Trigger extends Action {
    private List<Action> execute = new ArrayList<>();

    @Override
    public void stop(ActionContext actionContext, boolean recursive) {
        if (recursive) {
            execute.forEach(a -> a.stop(actionContext, true));
        }
        super.stop(actionContext, recursive);
    }

    @Override
    public int perform(ActionContext actionContext) {
        return runActions(new ActionContext(actionContext.wrapper(), actionContext.parent(), actionContext.performer(), actionContext.target()), execute);
    }
}
