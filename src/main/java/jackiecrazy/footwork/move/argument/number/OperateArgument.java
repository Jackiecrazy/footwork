package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;

public class OperateArgument implements Argument<Double> {
    private Argument<Double> first, second;
    private OPERATOR operation;

    @Override
    public Double resolve(ArgumentContext argumentContext) {
        double f = first.resolve(argumentContext);
        double s = second.resolve(argumentContext);
        return switch (operation) {
            case ADD -> f + s;
            case SUBTRACT -> f - s;
            case MULTIPLY -> f * s;
            case DIVIDE -> f / s;
            case MODULO -> f % s;
            case POWER -> Math.pow(f, s);
            case MAX -> Math.max(f, s);
            case MIN -> Math.min(f, s);
        };
    }


    public enum OPERATOR {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        MODULO("%"),
        POWER("^"),
        MAX("max"),
        MIN("min");

        private final String value;

        OPERATOR(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
}
