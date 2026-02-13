package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class IsTargetCondition extends Condition {
    private Argument<Entity> reference= CasterEntityArgument.INSTANCE;
    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        Entity ref=reference.resolve(argumentContext);
        return ref instanceof Mob mob && mob.getTarget() == argumentContext.target();
    }
}
