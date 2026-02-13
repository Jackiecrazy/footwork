package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class SelectorEntityArgument extends EntityArgument{
    private Argument<List<Entity>> select;
    @Override
    public Entity resolve(TimerActionsWrapper wrapper, Action parent, Entity caster, Entity target) {
        return select.resolve(wrapper, parent, caster, target).get(0);
    }
}
