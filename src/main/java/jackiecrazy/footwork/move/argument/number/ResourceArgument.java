package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.ResourceEnums;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ResourceArgument implements Argument<Double> {
    private Argument<Entity> reference_point = CasterEntityArgument.INSTANCE;
    private ResourceEnums.FORMAT format = ResourceEnums.FORMAT.CURRENT;
    private ResourceEnums.TYPE resource = ResourceEnums.TYPE.HEALTH;


    @Override
    public Double resolve(ArgumentContext argumentContext) {
        return reference_point.resolve(argumentContext) instanceof LivingEntity le ?
                format.resolve(le, resource) : 0;
    }
}
