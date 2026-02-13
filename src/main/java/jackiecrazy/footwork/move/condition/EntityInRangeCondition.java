package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.SelectorArgument;

public class EntityInRangeCondition extends Condition{
    private SelectorArgument select;

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        return !select.resolve(argumentContext).isEmpty();
    }
}
