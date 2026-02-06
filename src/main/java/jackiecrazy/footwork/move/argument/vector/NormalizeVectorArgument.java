package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class NormalizeVectorArgument extends VectorArgument{
    Argument<Vec3> from;

    @Override
    public Vec3 _resolve(ActionSetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return from.resolve(wrapper, parent, caster, target).normalize();
    }
}
