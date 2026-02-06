package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class GotoAction extends Action {
    private Argument<Double> instruction;

    @Override
    public int perform(ActionSetWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        return (int) instruction.resolve(wrapper, parent, performer, target).intValue();
    }
}
