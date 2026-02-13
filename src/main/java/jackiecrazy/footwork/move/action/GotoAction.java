package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class GotoAction extends Action {
    private Argument<Double> instruction;

    @Override
    public int perform(TimerActionsWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        return (int) instruction.resolve(wrapper, parent, performer, target).intValue();
    }
}
