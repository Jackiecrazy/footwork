package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class VectorLengthArgument implements Argument<Double> {
    private Argument<Vec3> of;

    @Override
    public Double resolve(ArgumentContext argumentContext) {
        Vec3 f = of.resolve(argumentContext);
        if(f!=null)
            return f.length();
        return 0d;
    }
}
