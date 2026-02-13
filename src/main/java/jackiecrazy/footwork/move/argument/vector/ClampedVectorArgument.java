package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClampedVectorArgument extends VectorArgument {
    Argument<Vec3> clamp;
    Argument<Double> max_x = FixedNumberArgument.MAX, max_y = FixedNumberArgument.MAX, max_z = FixedNumberArgument.MAX,
            min_x = FixedNumberArgument.MIN, min_y = FixedNumberArgument.MIN, min_z = FixedNumberArgument.MIN;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        Vec3 resolve = clamp.resolve(argumentContext);
        return new Vec3(
                Mth.clamp(resolve.x, min_x.resolve(argumentContext), max_x.resolve(argumentContext)),
                Mth.clamp(resolve.y, min_y.resolve(argumentContext), max_y.resolve(argumentContext)),
                Mth.clamp(resolve.z, min_z.resolve(argumentContext), max_z.resolve(argumentContext))
        );
    }
}
