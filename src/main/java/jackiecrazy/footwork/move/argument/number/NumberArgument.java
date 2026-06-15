package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;

public abstract class NumberArgument implements Argument<Double> {

    public abstract Double resolve(ArgumentContext argumentContext);


    public static class Store extends Action {
        private Argument<Entity> storage = CasterEntityArgument.INSTANCE;
        private Argument<Double> value;
        private String into;

        @Override
        public int perform(ActionContext actionContext) {
            final Double vec = value.resolve(actionContext);
            final Entity resolve = storage.resolve(actionContext);
            if (resolve != null && vec != null)
                resolve.getPersistentData().putDouble(into, vec);
            return 0;
        }
    }

    public static class Get implements Argument<Double> {
        private Argument<Entity> storage = CasterEntityArgument.INSTANCE;
        private String from;

        @Override
        public Double resolve(ArgumentContext argumentContext) {
            final Entity resolve = storage.resolve(argumentContext);
            if (resolve != null)
                return resolve.getPersistentData().getDouble(from);
            return 0D;
        }
    }
}
