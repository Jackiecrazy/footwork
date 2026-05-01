package jackiecrazy.footwork.move.argument;

import jackiecrazy.footwork.move.CircleEnums;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.vector.EyePositionVectorArgument;
import jackiecrazy.footwork.move.argument.vector.LookVectorArgument;
import jackiecrazy.footwork.move.filter.Filter;
import jackiecrazy.footwork.move.filter.NoFilter;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SelectorArgument implements Argument<List<Entity>> {
    //base point
    private CircleEnums.SWEEPTYPE shape;
    private Argument<Double> range= FixedNumberArgument.ZERO;
    private Argument<Double> width= FixedNumberArgument.ZERO;
    private Filter<Entity> filter;
    private Argument<Vec3> position;
    private Argument<Vec3> vector;

    public SelectorArgument() {
        shape = CircleEnums.SWEEPTYPE.CIRCLE;
        filter = NoFilter.INSTANCE;
        position = new EyePositionVectorArgument();
        vector = new LookVectorArgument();
    }

    public List<Entity> resolve(ArgumentContext argumentContext) {
        List<Entity> resolved = new ArrayList<>();
        Vec3 pos = position.resolve(argumentContext);
        Vec3 look = vector.resolve(argumentContext);
        double ra = range.resolve(argumentContext);
        if (shape == CircleEnums.SWEEPTYPE.NONE) {
            if (GeneralUtils.getDistSqCompensated(argumentContext.target(), pos) < ra * ra) resolved.add(argumentContext.target());
            return resolved;
        }
        double width = this.width.resolve(argumentContext);
        for (Entity ent : filter.filter(argumentContext, argumentContext.target().level().getEntities(null, new AABB(pos, pos).inflate(ra * 1.5)))) {
            //type specific sweep checks
            switch (shape) {
                case CONE -> {
                    if (!GeneralUtils.isFacingEntity(pos, look, ent, (int) width, 40)) continue;
                    if (GeneralUtils.getDistSqCompensated(ent, pos) > ra * ra) continue;
                }
                case CLEAVE -> {
                    if (!GeneralUtils.isFacingEntity(pos, look, ent, 40, (int) width)) continue;
                    if (GeneralUtils.getDistSqCompensated(ent, pos) > ra * ra) continue;
                }
                case CIRCLE -> {
                    if (GeneralUtils.getDistSqCompensated(ent, pos) > ra * ra) continue;
                }
                case LINE -> {
                    Vec3 end = pos.add(look.normalize().scale(width));
                    if (!ent.getBoundingBox().inflate(width).intersects(pos, end)) continue;
                }
            }
            resolved.add(ent);
        }
        return resolved;
    }
}
