package jackiecrazy.footwork.utils;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

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
            if (entity instanceof Mob && ((Mob) entity).getTarget() != null) {
                LivingEntity attack = ((Mob) entity).getTarget();
                return isAlly(attack, to);
            }
        }
        return false;
    }
}
