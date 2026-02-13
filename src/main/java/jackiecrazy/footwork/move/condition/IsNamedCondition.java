package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;

public class IsNamedCondition extends Condition {
    private Argument<Entity> reference= CasterEntityArgument.INSTANCE;
    private String name;
    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        Entity ref=reference.resolve(argumentContext);
        return ref.hasCustomName()&&ref.getCustomName().getString().equals(name);
    }
}
