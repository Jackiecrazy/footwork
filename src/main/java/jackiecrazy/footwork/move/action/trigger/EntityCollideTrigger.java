package jackiecrazy.footwork.move.action.trigger;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.utils.MovementUtils;
import net.minecraft.world.entity.Entity;

public class EntityCollideTrigger extends Trigger{
    @Override
    public boolean canRun(MovesetWrapper wrapper, Action parent, Entity performer, Entity target) {
        MovementUtils.collidingEntity(performer);
        return super.canRun(wrapper, parent, performer, target);
    }
}
