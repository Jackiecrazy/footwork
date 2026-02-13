package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public abstract class Condition implements Argument<Boolean> {

    public abstract Boolean resolve(ArgumentContext argumentContext);
}
