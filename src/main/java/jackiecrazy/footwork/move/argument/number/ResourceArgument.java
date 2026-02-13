package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.ResourceEnums;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ResourceArgument implements Argument<Double> {
    private Argument<Entity> reference_point = CasterEntityArgument.INSTANCE;
    private ResourceEnums.FORMAT format = ResourceEnums.FORMAT.CURRENT;
    private ResourceEnums.TYPE resource = ResourceEnums.TYPE.HEALTH;


    @Override
    public Double resolve(TimerActionsWrapper wrapper, Action parent, Entity caster, Entity target) {
        return reference_point.resolve(wrapper, parent, caster, target) instanceof LivingEntity le ?
                format.resolve(le, resource) : 0;
    }
}
