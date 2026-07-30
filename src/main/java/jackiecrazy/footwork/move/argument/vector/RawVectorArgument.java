package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class RawVectorArgument extends VectorArgument {
    public static final Argument<Vec3> ZERO = new VectorArgument() {

        @Override
        public Vec3 _resolve(ArgumentContext argumentContext) {
            return Vec3.ZERO;
        }
    };
    Argument<Double> x, y, z;

    public RawVectorArgument(){

    }

    public RawVectorArgument(Argument<Double> x, Argument<Double> y, Argument<Double> z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return new Vec3(x.resolve(argumentContext), y.resolve(argumentContext), z.resolve(argumentContext));
    }
}
