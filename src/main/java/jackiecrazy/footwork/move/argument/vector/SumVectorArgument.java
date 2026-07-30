package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class SumVectorArgument extends VectorArgument{
    Argument<Vec3>[] addends;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        Vec3 start=Vec3.ZERO;
        for(Argument<Vec3> vec: addends){
            start=start.add(vec.resolve(argumentContext));
        }
        return start;
    }
}
