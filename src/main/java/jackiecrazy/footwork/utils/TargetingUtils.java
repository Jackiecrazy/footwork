package jackiecrazy.footwork.utils;

import jackiecrazy.footwork.mixin.NearestAttackableTargetGoalAccessor;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class TargetingUtils {

    public static boolean isAlly(Entity entity, Entity of) {
        //what
        if (entity == null || of == null) return false;
        if (of == entity) return true;

        //entities with an owner
        if (entity instanceof OwnableEntity own && (own.getOwner() == of || isAlly(own.getOwner(), of)))
            return true;
        if (of instanceof OwnableEntity own && (own.getOwner() == entity || isAlly(own.getOwner(), entity)))
            return true;
        if (entity instanceof TraceableEntity p && (p.getOwner() == of || isAlly(p.getOwner(), of)))
            return true;
        if (of instanceof TraceableEntity p && (p.getOwner() == entity || isAlly(p.getOwner(), entity)))
            return true;

        //alliance check
        if (entity.isAlliedTo(of))
            return true;

        //player PvP check
        if (entity instanceof Player && of instanceof Player && entity.getServer() != null && !entity.getServer().isPvpAllowed())
            return true;
        return false;
    }

    public static boolean isHostile(Entity entity, Entity to) {
        if (entity == null || to == null) return false;
        if (isAlly(entity, to)) return false;
        if (entity instanceof LivingEntity) {
            if (((LivingEntity) entity).getLastHurtByMob() != null) {
                LivingEntity revenge = ((LivingEntity) entity).getLastHurtByMob();
                if (isAlly(revenge, to)) return true;
            }
            if (entity instanceof Mob m) {
                LivingEntity attack = m.getTarget();
                if (attack != null && isAlly(attack, to)) return true;
                for (WrappedGoal wg : m.goalSelector.getAvailableGoals()) {
                    if (wg.getGoal() instanceof NearestAttackableTargetGoal tg && ((NearestAttackableTargetGoalAccessor) tg).getTargetType().isAssignableFrom(to.getClass()))
                        return true;
                }
                return false;
            }
        }
        return false;
    }
}
