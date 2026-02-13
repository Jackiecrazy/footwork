package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CanSeeCondition extends Condition {
    private Argument<Entity> reference= CasterEntityArgument.INSTANCE;
    private Condition flimsy=FalseCondition.INSTANCE;
    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        Entity ref=reference.resolve(argumentContext);
        if(ref instanceof LivingEntity looker){
            return looker.hasLineOfSight(argumentContext.target());
        }
        return GeneralUtils.viewBlocked(ref, argumentContext.target(), flimsy.resolve(argumentContext));
    }
}
