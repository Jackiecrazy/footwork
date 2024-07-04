package jackiecrazy.footwork.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;

public class TargetingUtils {

    public static boolean isAlly(Entity entity, Entity of) {
        if (entity == null || of == null) return false;
        if (of == entity) return true;
        if (entity instanceof TamableAnimal && of instanceof LivingEntity && ((TamableAnimal) entity).isOwnedBy((LivingEntity) of))
            return true;
        if (of instanceof TamableAnimal && entity instanceof LivingEntity && ((TamableAnimal) of).isOwnedBy((LivingEntity) entity))
            return true;
        if (entity.isAlliedTo(of)) return true;
        if (entity instanceof Player && of instanceof Player && entity.getServer() != null && !entity.getServer().isPvpAllowed())
            return true;
        return false;
    }

    public static boolean isHostile(Entity entity, Entity to) {
        if (entity == null || to == null) return false;
        if (isAlly(entity, to)) return false;
        if (entity instanceof Player && to instanceof Player && entity.getServer() != null && entity.getServer().isPvpAllowed())
            return true;
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
