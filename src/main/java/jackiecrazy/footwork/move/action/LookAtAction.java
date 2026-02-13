package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class LookAtAction extends Action {
    private Argument<Entity> looker = CasterEntityArgument.INSTANCE;
    private Argument<Entity> entity_target;
    private Argument<Vec3> vector_target;
    private Argument<Double> head_rotation_x = new FixedNumberArgument(30);
    private Argument<Double> head_rotation_y = new FixedNumberArgument(30);
    private EntityAnchorArgument.Anchor anchor = EntityAnchorArgument.Anchor.EYES;

    @Override
    public int perform(ActionContext actionContext) {
        Entity toLook = looker.resolve(actionContext);
        if (vector_target != null)
            toLook.lookAt(anchor, vector_target.resolve(actionContext));
        if (toLook instanceof Mob e && entity_target != null) {
            e.getLookControl().setLookAt(entity_target.resolve(actionContext), (float) head_rotation_x.resolve(actionContext).floatValue(), (float) head_rotation_y.resolve(actionContext).floatValue());
        }
        return 0;
    }
}
