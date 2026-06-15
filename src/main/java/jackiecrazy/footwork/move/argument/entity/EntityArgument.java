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
        return resolve(wrapper.generateContext(caster, target, parent)).position();
    }

    public static class Store extends Action {
        private Argument<Entity> storage = CasterEntityArgument.INSTANCE;
        private Argument<Entity> value;
        private String into;

        @Override
        public int perform(ActionContext actionContext) {
            final Entity vec = value.resolve(actionContext);
            final Entity resolve = storage.resolve(actionContext);
            if (resolve != null && vec != null)
                resolve.getPersistentData().putInt(into, vec.getId());
            return 0;
        }
    }

    public static class Get implements Argument<Entity> {
        private Argument<Entity> storage = CasterEntityArgument.INSTANCE;
        private String from;

        @Override
        public Entity resolve(ArgumentContext argumentContext) {
            final Entity resolve = storage.resolve(argumentContext);
            if (resolve != null)
                return resolve.level().getEntity(resolve.getPersistentData().getInt(from));
            return null;
        }
    }
}
