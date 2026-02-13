package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class ParentTimerArgument extends NumberArgument {
    public static final ParentTimerArgument INSTANCE = new ParentTimerArgument();

    @Override
    public Double resolve(ArgumentContext argumentContext) {
        if (argumentContext instanceof ActionContext ta && ta.parent() instanceof TimerAction taa)
            return (double) ta.wrapper().getTimer(taa);
        return 0d;
    }
}
