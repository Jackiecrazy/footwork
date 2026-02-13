package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class RandomNumberArgument extends NumberArgument{
    Argument<Double> bound;
    @Override
    public Double resolve(ArgumentContext argumentContext) {
        return Footwork.rand.nextDouble(bound.resolve(argumentContext));
    }
}
