package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class DebugAction extends Action {
    private Argument<?> parameter;
    @Override
    public int perform(ActionContext actionContext) {
        System.out.println("here are the performer and target:");
        System.out.println(actionContext.performer());
        System.out.println(actionContext.target());
        if (parameter != null)
            System.out.println("the parameter resolves to " + parameter.resolve(actionContext).toString());
        return 0;
    }
}
