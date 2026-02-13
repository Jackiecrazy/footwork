package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class DistanceArgument implements Argument<Double> {
    private Argument<?> first, second;

    @Override
    public Double resolve(ArgumentContext argumentContext) {
        Object f = first.resolve(argumentContext), s = second.resolve(argumentContext);
        Vec3 from = null, to = null;
        if (f instanceof Entity e)
            from = e.position();
        else if (f instanceof Vec3 e)
            from = e;
        else if (f instanceof BlockPos e)
            from = e.getCenter();
        if (s instanceof Entity e)
            to = e.position();
        else if (s instanceof Vec3 e)
            to = e;
        else if (s instanceof BlockPos e)
            to = e.getCenter();
        if (from != null && to != null)
            return from.distanceTo(to);
        return 0d;
    }
}
