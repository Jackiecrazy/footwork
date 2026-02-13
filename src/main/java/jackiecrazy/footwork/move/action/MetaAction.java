package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.utils.ActionContext;

import java.util.ArrayList;
import java.util.List;

public class MetaAction extends Action{
    private List<Action> actions=new ArrayList<>();

    public MetaAction() {
        super();
    }

    public MetaAction(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public boolean repeatable(ActionContext actionContext) {
        return true;
    }

    @Override
    public int perform(ActionContext actionContext) {
        return runActions(new ActionContext(actionContext.wrapper(), actionContext.parent(), actionContext.performer(), actionContext.target()), actions);
    }
}