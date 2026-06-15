package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class ExistsCondition extends Condition {
    private Argument<?> of;

    public static ExistsCondition create(Argument<?> of) {
        ExistsCondition ret = new ExistsCondition();
        ret.of = of;
        return ret;
    }

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        return of.resolve(argumentContext) != null;
    }
}
