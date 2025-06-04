package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.CombatCircle;
import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class RandomFilter<T> extends Filter<T> {
    public static final RandomFilter<?> INSTANCE=new RandomFilter();
    @Override
    public List<T> filter(MovesetWrapper wrapper, Action parent, Entity performer, Entity target, List<T> targets) {
        List<T> ret = new ArrayList<>();
        while (ret.size() < limit && targets.size() > 1) {
            int index= CombatCircle.rand.nextInt(targets.size());
            ret.add(targets.remove(index));
        }
        return ret;
    }
}
