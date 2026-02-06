package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import net.minecraft.world.entity.Entity;

public class ParentTimerArgument extends NumberArgument{
public static final ParentTimerArgument INSTANCE=new ParentTimerArgument();
    @Override
    public Double resolve(ActionSetWrapper wrapper, Action parent, Entity caster, Entity target) {
        if(!(parent instanceof TimerAction ta))return 0.0;
        return (double) wrapper.getTimer(ta);
    }
}
