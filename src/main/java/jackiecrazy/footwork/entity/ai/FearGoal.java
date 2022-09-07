package jackiecrazy.footwork.entity.ai;

import jackiecrazy.footwork.capability.goal.GoalCapabilityProvider;
import jackiecrazy.footwork.capability.goal.IGoalHelper;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class FearGoal extends AvoidEntityGoal<LivingEntity> {
    public FearGoal(PathfinderMob entityIn) {
        super(entityIn, LivingEntity.class, 16, 1.3, 1.6);
    }

    public boolean canUse() {
        if (!mob.hasEffect(FootworkEffects.FEAR.get())) return false;
        this.toAvoid = mob.getKillCredit();
        final Optional<IGoalHelper> goal = GoalCapabilityProvider.getCap(mob).resolve();
        goal.ifPresent(iGoalHelper -> toAvoid = iGoalHelper.getFearSource());
        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3 vector3d = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
            if (vector3d == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vector3d.x, vector3d.y, vector3d.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath(vector3d.x, vector3d.y, vector3d.z, 0);
                return this.path != null;
            }
        }
    }
}
