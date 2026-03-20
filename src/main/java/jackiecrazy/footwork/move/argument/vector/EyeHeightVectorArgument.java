package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EyeHeightVectorArgument extends VectorArgument {
    private Argument<Entity> reference_point;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return new Vec3(0, reference_point.resolve(argumentContext).getEyeHeight(), 0);
    }
}
