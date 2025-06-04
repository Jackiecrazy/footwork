package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;

public class IsNamedCondition extends Condition {
    private Argument<Entity> reference= CasterEntityArgument.INSTANCE;
    private String name;
    @Override
    public Boolean resolve(MovesetWrapper wrapper, Action parent, Entity performer, Entity target) {
        Entity ref=reference.resolve(wrapper, parent, performer, target);
        return ref.hasCustomName()&&ref.getCustomName().getString().equals(name);
    }
}
