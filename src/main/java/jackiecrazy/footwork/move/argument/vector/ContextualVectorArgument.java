package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.phys.Vec3;

public class ContextualVectorArgument extends VectorArgument {
    public static final ContextualVectorArgument INSTANCE=new ContextualVectorArgument();

    @Override
    public Vec3 _resolve(ArgumentContext argumentContext) {
        return argumentContext.getContext("position");
    }
}
