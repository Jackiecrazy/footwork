package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class MultiplyVectorArgument extends VectorArgument {
    Argument<Vec3> first;
    Argument<Vec3> second;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return first.resolve(argumentContext).multiply(second.resolve(argumentContext));
    }
}
