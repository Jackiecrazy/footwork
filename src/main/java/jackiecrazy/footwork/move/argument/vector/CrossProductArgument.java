package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CrossProductArgument extends VectorArgument {
    private Argument<Vec3> first, second;

    @Override
    public Vec3 _resolve(TimerActionsWrapper wrapper, Action parent, Entity caster, Entity target) {
        return first.resolve(wrapper, parent, caster, target).cross(second.resolve(wrapper, parent, caster, target));
    }
}
