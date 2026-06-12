package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.condition.TrueCondition;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import jackiecrazy.footwork.move.Move;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Action extends Move {
    protected Condition condition = TrueCondition.INSTANCE;
    protected Condition repeatable = FalseCondition.INSTANCE;
    protected boolean logErrors=false;

    public boolean logsErrors() {
        return logErrors;
    }

    protected String ID = "(default)";

    /**
     * Runs the list of actions, aborting and returning a jump code if the child returns a jump code.
     */
    protected int runActions(ActionContext actionContext, @Nullable List<Action> actions) {
        if (actions == null) return 0;
        int returnCode = 0;
        for (Action child : actions) {
            if (child.canRun(actionContext)) {
                returnCode = actionContext.wrapper().trigger(child, actionContext.parent(), actionContext.performer(), actionContext.target());
                if (returnCode > 0) return returnCode;
            }
        }
        return returnCode;
    }

    /**
     * @return 0 for normal execution. -1 is reserved for expiry of timer actions, and any positive integer is taken to be a jump code.
     */
    public abstract int perform(ActionContext actionContext);

    public String serializeToJson() {
        return ActionJsonAdapters.gson.toJson(this);
    }

    public boolean canRun(ActionContext actionContext) {
        if (actionContext.wrapper().getGraveyard().contains(this)) return false;
        return condition.resolve(actionContext);
    }

    public boolean repeatable(ActionContext actionContext) {
        return repeatable.resolve(actionContext);
    }


    public void stop(ActionContext actionContext, boolean recursive) {
    }

    public String toString() {
        return ID;
    }
}
