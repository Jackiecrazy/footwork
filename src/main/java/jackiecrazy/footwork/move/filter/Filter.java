package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.move.Move;
import jackiecrazy.footwork.move.utils.ArgumentContext;

import java.util.List;

public abstract class Filter<T> extends Move {
    protected String ID;
    protected int limit;

    public abstract List<T> filter(ArgumentContext argumentContext, List<T> targets);

    public String toString() {
        return ID;
    }
}
