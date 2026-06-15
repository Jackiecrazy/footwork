package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RayTraceEntityArgument extends EntityArgument {
    Argument<Entity> from = CasterEntityArgument.INSTANCE;
    Argument<Vec3> position;
    Argument<Vec3> look;
    Argument<Double> distance;

    @Override
    public Entity resolve(ArgumentContext argumentContext) {
        Entity start = from.resolve(argumentContext);
        Double range = distance.resolve(argumentContext);
        if (start == null || range == null) return null;
        Vec3 position = this.position == null ? start.position() : this.position.resolve(argumentContext);
        Vec3 look = this.look == null ? start.getLookAngle() : this.look.resolve(argumentContext);
        if (position == null || look == null) return null;
        final List<Entity> entities = GeneralUtils.raytraceEntities(argumentContext.performer().level(), start, position, look, range);
        return entities.isEmpty() ? null : entities.get(0);
    }
}
