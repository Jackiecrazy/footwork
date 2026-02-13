package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;

public class IsTypeCondition extends Condition {
    private Argument<Entity> reference = TargetEntityArgument.INSTANCE;
    private MobType type;//fixme not serializable

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        Entity ref = reference.resolve(argumentContext);
        return ref instanceof Mob mob &&mob.getMobType().equals(type);
    }
}
