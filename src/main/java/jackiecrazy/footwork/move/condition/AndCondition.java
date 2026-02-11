package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class AndCondition extends Condition {
    public AndCondition() {
    }

    public AndCondition(List<Condition> values) {
        this.values = values;
    }

    List<Condition> values;

    @Override
    public Boolean resolve(ActionSetWrapper wrapper, Action parent, Entity performer, Entity target) {
        for(Condition c: values){
            if(!c.resolve(wrapper, parent, performer, target))return false;
        }
        return true;
    }
}
