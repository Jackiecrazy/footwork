package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;

public class TargetEntityArgument extends EntityArgument{
    public static final TargetEntityArgument INSTANCE=new TargetEntityArgument();
    @Override
    public Entity resolve(ArgumentContext argumentContext) {
        return argumentContext.target();
    }
}
