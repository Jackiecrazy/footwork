package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.utils.ArgumentContext;

import java.util.List;

public class OrCondition extends Condition {
    List<Condition> values;

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        for(Condition c: values){
            if(c.resolve(argumentContext))return true;
        }
        return false;
    }
}
