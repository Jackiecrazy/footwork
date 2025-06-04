package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.CombatCircle;
import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;

public class RandomNumberArgument extends NumberArgument{
    Argument<Double> bound;
    @Override
    public Double resolve(MovesetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return CombatCircle.rand.nextDouble(bound.resolve(wrapper, parent, caster, target));
    }
}
