package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.utils.ArgumentContext;

public class FixedNumberArgument extends NumberArgument{
    public static final FixedNumberArgument ZERO = new FixedNumberArgument(0);
    public static final FixedNumberArgument ONE = new FixedNumberArgument(1);
    public static final FixedNumberArgument MAX = new FixedNumberArgument(Integer.MAX_VALUE);
    public static final FixedNumberArgument MIN = new FixedNumberArgument(Integer.MIN_VALUE);
    double number;

    public FixedNumberArgument(double number) {
        this.number = number;
    }

    public FixedNumberArgument() {
    }

    @Override
    public Double resolve(ArgumentContext argumentContext) {
        return number;
    }
}
