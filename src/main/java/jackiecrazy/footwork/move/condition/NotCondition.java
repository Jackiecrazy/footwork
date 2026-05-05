package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.utils.ArgumentContext;

public class NotCondition extends Condition {
    Condition of;

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        return !of.resolve(argumentContext);
    }

    public static NotCondition of(Condition c){
        NotCondition ret =new NotCondition();
        ret.of=c;
        return ret;
    }
}
