package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.condition.Condition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;

public class SetAggressiveAction extends Action {
    Condition toggle;

    @Override
    public int perform(ActionSetWrapper wrapper, Action parent, @Nullable Entity performer, Entity target) {
        if (performer instanceof Mob e) {
            e.setAggressive(toggle.resolve(wrapper, parent, performer, target));
        }
        return 0;
    }
}
