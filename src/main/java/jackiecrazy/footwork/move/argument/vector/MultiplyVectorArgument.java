package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class MultiplyVectorArgument extends VectorArgument {
    Argument<Vec3> base;
    Argument<Vec3> multiply;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return base.resolve(argumentContext).multiply(multiply.resolve(argumentContext));
    }
}
