package jackiecrazy.footwork.move.argument;

import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class ParentDataArgument<T> implements Argument<T> {
    public static final ParentDataArgument<?> INSTANCE = new ParentDataArgument<>();

    @Override
    public T resolve(ArgumentContext argumentContext) {
        if (argumentContext instanceof ActionContext ac)
            return ac.wrapper().getData(ac.parent());
        return null;
    }
}
