package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public abstract class Condition implements Argument<Boolean> {

    public abstract Boolean resolve(TimerActionsWrapper wrapper, Action parent, @Nullable Entity performer, Entity target);
}
