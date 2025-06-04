package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CurrentHealthArgument implements Argument<Double> {
    private Argument<Entity> reference_point;

    @Override
    public Double resolve(MovesetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return (double) (reference_point.resolve(wrapper, parent, caster, target) instanceof LivingEntity le ? le.getHealth() : 0);
    }
}
