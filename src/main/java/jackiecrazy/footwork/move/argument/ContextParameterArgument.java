package jackiecrazy.footwork.move.argument;

import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class ContextParameterArgument<T> implements Argument<T> {
    private String key;

    @Override
    public T resolve(ArgumentContext argumentContext) {
        return argumentContext.getContext(key);
    }
}
