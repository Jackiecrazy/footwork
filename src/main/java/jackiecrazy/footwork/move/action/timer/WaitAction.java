package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class WaitAction extends TimerAction {
    private List<Action> waiting = new ArrayList<>();

    @Override
    public int tick(ActionSetWrapper wrapper, Entity performer, Entity target) {
        int childRet=runActions(wrapper.generateContext(performer, target, this), waiting);
        if(childRet!=0)return childRet;
        return super.tick(wrapper, performer, target);
    }

    public void stop(ActionContext actionContext, boolean recursive) {
        if (recursive) {
            waiting.forEach(a -> a.stop(actionContext, true));
        }
        super.stop(actionContext, recursive);
    }

}
