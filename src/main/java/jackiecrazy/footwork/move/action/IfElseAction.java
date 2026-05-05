package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

import java.util.ArrayList;
import java.util.List;

public class IfElseAction extends Action{
    private List<Action> then=new ArrayList<>();
    private List<Action> otherwise=new ArrayList<>();

    @Override
    public boolean canRun(ActionContext actionContext) {
        return true;
    }

    @Override
    public boolean repeatable(ActionContext actionContext) {
        return true;
    }

    @Override
    public int perform(ActionContext actionContext) {
        if(condition.resolve(actionContext)){
            return runActions(actionContext, then);
        }else return runActions(actionContext, otherwise);
    }
}
