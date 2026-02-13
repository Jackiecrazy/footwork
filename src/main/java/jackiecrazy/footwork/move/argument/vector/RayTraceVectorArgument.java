package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.TrueCondition;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public class RayTraceVectorArgument extends VectorArgument {
    Argument<Vec3> position;
    Argument<Vec3> direction;
    Argument<Double> distance;
    Condition scans_entities = TrueCondition.INSTANCE;
    ClipContext.Block block_clip = ClipContext.Block.COLLIDER;
    ClipContext.Fluid fluid_clip = ClipContext.Fluid.NONE;

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        Vec3 start = position.resolve(argumentContext);
        Vec3 look = direction.resolve(argumentContext);
        double range = distance.resolve(argumentContext);
        return GeneralUtils.raytraceAnything(argumentContext.performer().level(), start, look, range, scans_entities.resolve(argumentContext), block_clip, fluid_clip).getLocation();
    }
}
