package jackiecrazy.footwork.move.action.trigger;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Trigger extends Action {
    private List<Action> execute = new ArrayList<>();

    @Override
    public void stop(ActionSetWrapper wrapper, Entity performer, Entity target, boolean recursive) {
        if (recursive) {
            execute.forEach(a -> a.stop(wrapper, performer, target, true));
        }
        super.stop(wrapper, performer, target, recursive);
    }

    @Override
    public int perform(ActionSetWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        return runActions(wrapper, parent, execute, performer, target);
    }
}
