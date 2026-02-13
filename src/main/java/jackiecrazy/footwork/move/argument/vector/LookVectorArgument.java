package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class LookVectorArgument extends VectorArgument{
    Argument<Entity> reference_point=CasterEntityArgument.INSTANCE;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return reference_point.resolve(argumentContext).getLookAngle();
    }
}
