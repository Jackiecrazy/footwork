package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class NormalizeVectorArgument extends VectorArgument{
    Argument<Vec3> from;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return from.resolve(argumentContext).normalize();
    }
}
