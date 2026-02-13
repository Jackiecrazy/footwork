package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PositionVectorArgument extends VectorArgument {
    private Argument<Entity> reference_point= CasterEntityArgument.INSTANCE;
    public static final PositionVectorArgument CASTER = new PositionVectorArgument();

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return reference_point.resolve(argumentContext).position();
    }
}
