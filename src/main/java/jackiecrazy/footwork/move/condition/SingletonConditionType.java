package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.SingletonArgumentType;

/**
 only implemented in lambdas in ActionRegistry.
 */
public class SingletonConditionType extends SingletonArgumentType<Boolean> {
    public SingletonConditionType(Class<?> of, Argument<Boolean> instance) {
        super(of, instance);
    }
}
