package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HealthPercArgument extends NumberArgument{
    private Argument<Entity> reference_point;

    @Override
    public Double resolve(ActionSetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return reference_point.resolve(wrapper, parent, caster, target) instanceof LivingEntity le ? (double)(le.getHealth()/le.getMaxHealth()) : 1.0;
    }
}
