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

    public boolean m_8036_() {
        if (!f_25015_.m_21023_(FootworkEffects.FEAR.get())) return false;
        this.f_25016_ = f_25015_.m_21232_();
        final Optional<IGoalHelper> goal = GoalCapabilityProvider.getCap(f_25015_).resolve();
        goal.ifPresent(iGoalHelper -> f_25016_ = iGoalHelper.getFearSource());
        if (this.f_25016_ == null) {
            return false;
        } else {
            Vec3 vector3d = DefaultRandomPos.m_148407_(this.f_25015_, 16, 7, this.f_25016_.m_20182_());
            if (vector3d == null) {
                return false;
            } else if (this.f_25016_.m_20275_(vector3d.f_82479_, vector3d.f_82480_, vector3d.f_82481_) < this.f_25016_.m_20280_(this.f_25015_)) {
                return false;
            } else {
                this.f_25018_ = this.f_25019_.m_26524_(vector3d.f_82479_, vector3d.f_82480_, vector3d.f_82481_, 0);
                return this.f_25018_ != null;
            }
        }
    }
}
