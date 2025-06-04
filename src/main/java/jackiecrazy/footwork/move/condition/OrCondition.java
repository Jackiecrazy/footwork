package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class OrCondition extends Condition {
    List<Condition> values;

    @Override
    public Boolean resolve(MovesetWrapper wrapper, Action parent, Entity performer, Entity target) {
        for(Condition c: values){
            if(c.resolve(wrapper, parent, performer, target))return true;
        }
        return false;
    }
}
