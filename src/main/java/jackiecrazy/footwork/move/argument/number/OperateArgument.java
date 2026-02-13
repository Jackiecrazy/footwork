package jackiecrazy.footwork.move.argument.number;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;

public class OperateArgument implements Argument<Double> {
    private Argument<Double> first, second;
    private OPERATOR operation;

    @Override
    public Double resolve(TimerActionsWrapper wrapper, Action parent, Entity performer, Entity target) {
        double f = first.resolve(wrapper, parent, performer, target);
        double s = second.resolve(wrapper, parent, performer, target);
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
            return this.value; //will return , or ' instead of COMMA or APOSTROPHE
        }
    }
}
