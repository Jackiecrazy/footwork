package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.utils.ArgumentContext;

public class TrueCondition extends Condition {
    public static final TrueCondition INSTANCE = new TrueCondition();

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        return true;
    }
}
