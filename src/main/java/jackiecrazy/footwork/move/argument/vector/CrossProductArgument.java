package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class CrossProductArgument extends VectorArgument {
    private Argument<Vec3> first, second;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return first.resolve(argumentContext).cross(second.resolve(argumentContext));
    }
}
