package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.utils.ActionContext;

public class StopAction extends Action {
    boolean recursive=true;
    @Override
    public int perform(ActionContext actionContext) {
        actionContext.parent().stop(actionContext, recursive);
        return -1;
    }
}
