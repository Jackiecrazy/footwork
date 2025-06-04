package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class DotProductArgument implements Argument<Double> {
    private Argument<Vec3> first, second;

    @Override
    public Double resolve(MovesetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return first.resolve(wrapper, parent, caster, target).dot(second.resolve(wrapper, parent, caster, target));
    }
}
