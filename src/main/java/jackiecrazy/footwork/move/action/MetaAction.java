package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

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
    public boolean repeatable(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target) {
        return true;
    }

    @Override
    public int perform(TimerActionsWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        return runActions(wrapper, parent, actions, performer, target);
    }
}