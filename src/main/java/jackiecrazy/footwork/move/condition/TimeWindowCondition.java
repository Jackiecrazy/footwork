package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class TimeWindowCondition extends Condition {

    private int from = 0, to = Integer.MAX_VALUE, every = 1;

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        int time = 0;
        if (argumentContext instanceof ActionContext ta && ta.parent() instanceof TimerAction taa)
            time = ta.wrapper().getTimer(taa);
        return time >= from && time < to && (time - from) % every == 0;
    }
}
