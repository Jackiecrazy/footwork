package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.move.utils.ArgumentContext;

import java.util.List;

public class NoFilter<T> extends Filter<T> {
    public static final NoFilter INSTANCE=new NoFilter();
    @Override
    public List<T> filter(ArgumentContext argumentContext, List<T> targets) {
        return targets;
    }
}
