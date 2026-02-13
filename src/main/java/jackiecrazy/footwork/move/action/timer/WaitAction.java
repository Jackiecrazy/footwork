package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class WaitAction extends TimerAction {
    private List<Action> waiting = new ArrayList<>();

    @Override
    public int tick(TimerActionsWrapper wrapper, Entity performer, Entity target) {
        int childRet=runActions(wrapper, this, waiting, performer, target);
        if(childRet!=0)return childRet;
        return super.tick(wrapper, performer, target);
    }

    public void stop(TimerActionsWrapper wrapper, Entity performer, Entity target, boolean recursive) {
        if (recursive) {
            waiting.forEach(a -> a.stop(wrapper, performer, target, true));
        }
        super.stop(wrapper, performer, target, recursive);
    }

}
