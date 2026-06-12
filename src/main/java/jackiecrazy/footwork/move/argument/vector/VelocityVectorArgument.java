package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class VelocityVectorArgument extends VectorArgument {
    Argument<Entity> reference_point = CasterEntityArgument.INSTANCE;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        final Entity e = reference_point.resolve(argumentContext);
        if (e != null)
            return e.getDeltaMovement();
        return null;
    }
}
