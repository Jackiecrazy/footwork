package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class RNGCondition extends Condition{
    private Argument<Double> chance;
    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        return Footwork.rand.nextDouble()<chance.resolve(argumentContext);
    }
}
