package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.DamageArgument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.vector.PositionVectorArgument;
import jackiecrazy.footwork.move.argument.vector.RawVectorArgument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.utils.GeneralUtils;
import jackiecrazy.footwork.utils.MovementUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class KnockbackAction extends Action {

    private Argument<Double> strength = new FixedNumberArgument(5);
    private Argument<Vec3> direction = (a) -> new Vec3(0, 0.02, 0.98);
    private Argument<Entity> from = CasterEntityArgument.INSTANCE;
    private Argument<Entity> to = TargetEntityArgument.INSTANCE;
    private Condition force = FalseCondition.INSTANCE;

    @Override
    public int perform(ActionContext actionContext) {
        Entity from = this.from.resolve(actionContext), to = this.to.resolve(actionContext);
        Vec3 d = direction.resolve(actionContext);
        Boolean force = this.force.resolve(actionContext);
        Double str = this.strength.resolve(actionContext);
        if (from == null || to == null || d == null || force == null || str == null) return 0;
        Vec3 recalc = MovementUtils.resolveVelocity(to.position().vectorTo(from.position()), d);
        MovementUtils.knockBack(to, str.floatValue(), recalc.x, recalc.y, recalc.z, force);
        return 0;
    }
}
