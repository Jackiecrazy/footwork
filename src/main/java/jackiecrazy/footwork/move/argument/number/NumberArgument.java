package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public abstract class NumberArgument implements Argument<Double> {

    public abstract Double resolve(ArgumentContext argumentContext);


    public static class Store extends Action {
        private Argument<Double> value;
        private String into;

        @Override
        public int perform(ActionContext actionContext) {
            final double vec = value.resolve(actionContext);
            actionContext.performer().getPersistentData().putDouble(into, vec);
            return 0;
        }
    }

    public static class Get implements Argument<Double> {
        private String from;

        @Override
        public Double resolve(ArgumentContext argumentContext) {
            return argumentContext.performer().getPersistentData().getDouble(from);
        }
    }
}
