package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.utils.ArgumentContext;

public class FalseCondition extends Condition{
    public static final FalseCondition INSTANCE=new FalseCondition();
    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        return false;
    }
}
