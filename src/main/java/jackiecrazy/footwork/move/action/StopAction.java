package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class StopAction extends Action {
    boolean recursive=true;
    @Override
    public int perform(TimerActionsWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        parent.stop(wrapper, performer, target, recursive);
        return -1;
    }
}
