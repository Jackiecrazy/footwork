package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class DotProductArgument implements Argument<Double> {
    private Argument<Vec3> first, second;

    @Override
    public Double resolve(ArgumentContext argumentContext) {
        return first.resolve(argumentContext).dot(second.resolve(argumentContext));
    }
}
