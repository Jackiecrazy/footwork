package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PositionVectorArgument extends VectorArgument {
    public static final PositionVectorArgument CASTER = new PositionVectorArgument();
    public static final PositionVectorArgument TARGET = new PositionVectorArgument().setReference_point(TargetEntityArgument.INSTANCE);
    private Argument<Entity> reference_point = CasterEntityArgument.INSTANCE;

    public PositionVectorArgument setReference_point(Argument<Entity> reference_point) {
        this.reference_point = reference_point;
        return this;
    }

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        final Entity resolve = reference_point.resolve(argumentContext);
        if (resolve != null)
            return resolve.position();
        return null;
    }
}
