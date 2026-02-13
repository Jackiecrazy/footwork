package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class GotoAction extends Action {
    private Argument<Double> instruction;

    @Override
    public int perform(ActionContext actionContext) {
        return (int) instruction.resolve(actionContext).intValue();
    }
}
