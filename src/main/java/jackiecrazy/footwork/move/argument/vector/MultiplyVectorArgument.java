package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MultiplyVectorArgument extends VectorArgument {
    Argument<Vec3> base;
    Argument<Vec3> multiply;

    @Override
    public Vec3 _resolve(MovesetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return base.resolve(wrapper, parent, caster, target).multiply(multiply.resolve(wrapper, parent, caster, target));
    }
}
