package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class EntityArgument implements Argument<Entity> {
    public abstract Entity resolve(ArgumentContext argumentContext);

    public Vec3 _resolve(ActionSetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return resolve(new ActionContext(wrapper, parent, caster, target)).position();
    }

    public static class Store extends Action {
        private Argument<Entity> value;
        private String into;

        @Override
        public int perform(ActionContext actionContext) {
            final Entity vec = value.resolve(actionContext);
            actionContext.performer().getPersistentData().putInt(into, vec.getId());
            return 0;
        }
    }

    public static class Get implements Argument<Entity> {
        private String from;

        @Override
        public Entity resolve(ArgumentContext argumentContext) {
            return argumentContext.performer().level().getEntity(argumentContext.performer().getPersistentData().getInt(from));
        }
    }
}
