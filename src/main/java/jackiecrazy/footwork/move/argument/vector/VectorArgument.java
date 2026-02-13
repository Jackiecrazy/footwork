package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.world.phys.Vec3;

public abstract class VectorArgument implements Argument<Vec3> {
    private Argument<Double> scale = FixedNumberArgument.ONE;
    private Argument<Double> min_length = FixedNumberArgument.ZERO;
    private Argument<Double> max_length = FixedNumberArgument.ZERO;

    public Vec3 resolve(ArgumentContext argumentContext) {
        double minLength = min_length.resolve(argumentContext);
        double maxLength = max_length.resolve(argumentContext);
        double sc = scale.resolve(argumentContext);
        Vec3 ret = _resolve(argumentContext).scale(sc);
        if (minLength > 0 && ret.lengthSqr() < minLength * minLength) {
            ret=ret.scale(minLength / ret.length());
        }
        if (maxLength > 0 && ret.lengthSqr() > maxLength * maxLength) {
            ret=ret.scale(maxLength / ret.length());
        }
        return ret;
    }

    public abstract Vec3 _resolve(ArgumentContext argumentContext);

    public static class Store extends Action{
        private Argument<Vec3> value;
        private String into;
        @Override
        public int perform(ActionContext actionContext) {
            final Vec3 vec=value.resolve(actionContext);
            actionContext.performer().getPersistentData().putDouble(into +"_x", vec.x);
            actionContext.performer().getPersistentData().putDouble(into +"_y", vec.y);
            actionContext.performer().getPersistentData().putDouble(into +"_z", vec.z);
            return 0;
        }
    }

    public static class Get extends VectorArgument{
        private String from;

        @Override
        public Vec3 _resolve(ArgumentContext argumentContext) {
            return new Vec3(argumentContext.performer().getPersistentData().getDouble(from +"_x"), argumentContext.performer().getPersistentData().getDouble(from +"_y"), argumentContext.performer().getPersistentData().getDouble(from +"_z"));
        }
    }
}
