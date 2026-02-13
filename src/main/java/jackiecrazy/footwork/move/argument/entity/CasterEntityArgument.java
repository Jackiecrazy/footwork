package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;

public class CasterEntityArgument extends EntityArgument{
    public static final CasterEntityArgument INSTANCE=new CasterEntityArgument();
    @Override
    public Entity resolve(ArgumentContext argumentContext) {
        return argumentContext.performer();
    }
}
