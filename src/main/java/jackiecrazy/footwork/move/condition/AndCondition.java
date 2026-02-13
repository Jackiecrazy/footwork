package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.utils.ArgumentContext;

import java.util.List;

public class AndCondition extends Condition {
    public AndCondition() {
    }

    public AndCondition(List<Condition> values) {
        this.values = values;
    }

    List<Condition> values;

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        for(Condition c: values){
            if(!c.resolve(argumentContext))return false;
        }
        return true;
    }
}
