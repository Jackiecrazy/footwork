package jackiecrazy.footwork.move.argument.vector;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EyePositionVectorArgument extends VectorArgument {
    private Argument<Entity> reference_point= CasterEntityArgument.INSTANCE;

    @Override
    public Vec3 _resolve(ActionSetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return reference_point.resolve(wrapper, parent, caster, target).getEyePosition();
    }
}
