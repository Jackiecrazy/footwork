package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class RandomFilter<T> extends Filter<T> {
    public static final RandomFilter<?> INSTANCE=new RandomFilter();
    @Override
    public List<T> filter(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target, List<T> targets) {
        List<T> ret = new ArrayList<>();
        while (ret.size() < limit && targets.size() > 1) {
            int index= Footwork.rand.nextInt(targets.size());
            ret.add(targets.remove(index));
        }
        return ret;
    }
}
