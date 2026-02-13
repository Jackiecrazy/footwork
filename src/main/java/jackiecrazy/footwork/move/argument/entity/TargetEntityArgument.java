package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

public class TargetEntityArgument extends EntityArgument{
    public static final TargetEntityArgument INSTANCE=new TargetEntityArgument();
    @Override
    public Entity resolve(TimerActionsWrapper wrapper, Action parent, Entity caster, Entity target) {
        return target;
    }
}
