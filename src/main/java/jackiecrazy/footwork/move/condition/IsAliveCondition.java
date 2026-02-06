package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;

public class IsAliveCondition extends Condition {
    private Argument<Entity> reference= CasterEntityArgument.INSTANCE;
    @Override
    public Boolean resolve(ActionSetWrapper wrapper, Action parent, Entity performer, Entity target) {
        Entity ref=reference.resolve(wrapper, parent, performer, target);
        return ref.isAlive();
    }
}
