package jackiecrazy.footwork.move.action.timer;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectHitboxAction extends TimerAction {
    public static boolean multihit_override=false;
    /**
     * 0 for once only, other hitboxes repeat every x ticks.
     */
    private int hit_cooldown;
    private Argument<List<Entity>> selector;
    private List<Action> actions = new ArrayList<>();

    @Override
    public void start(ActionSetWrapper wrapper, Entity performer, Entity target) {
        wrapper.setData(this, new HashMap<>());
    }

    @Override
    public int tick(ActionSetWrapper wrapper, Entity performer, Entity target) {
        HashMap<Entity, Long> lastHit = wrapper.getData(this);
        multihit_override=true;
        for (Entity e : selector.resolve(wrapper.generateContext(performer, target, this))) {
            if (hit_cooldown == 0 && lastHit.containsKey(e)) continue;
            if (lastHit.containsKey(e) && lastHit.get(e) + hit_cooldown < e.level().getGameTime()) continue;
            int childRet = runActions(wrapper.generateContext(performer, e, this), actions);
            if (childRet != 0) return childRet;
            lastHit.put(e, e.level().getGameTime());
        }
        multihit_override=false;
        return super.tick(wrapper, performer, target);
    }

    public void stop(ActionContext actionContext, boolean recursive) {
        if (recursive) {
            actions.forEach(a -> a.stop(actionContext, true));
        }
        super.stop(actionContext, recursive);
    }

}
