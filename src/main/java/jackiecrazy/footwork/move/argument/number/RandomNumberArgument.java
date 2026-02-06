package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;

public class RandomNumberArgument extends NumberArgument{
    Argument<Double> bound;
    @Override
    public Double resolve(ActionSetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return Footwork.rand.nextDouble(bound.resolve(wrapper, parent, caster, target));
    }
}
