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
        if (entity instanceof TamableAnimal && of instanceof LivingEntity && ((TamableAnimal) entity).m_21830_((LivingEntity) of))
            return true;
        if (of instanceof TamableAnimal && entity instanceof LivingEntity && ((TamableAnimal) of).m_21830_((LivingEntity) entity))
            return true;
        if (entity.m_7307_(of)) return true;
        if (entity instanceof Player && of instanceof Player && entity.m_20194_() != null && entity.m_20194_().m_129799_())
            return true;
        return false;
    }

    public static boolean isHostile(Entity entity, Entity to) {
        if (entity == null || to == null) return false;
        if (isAlly(entity, to)) return false;
        if (entity instanceof LivingEntity) {
            if (((LivingEntity) entity).m_142581_() != null) {
                LivingEntity revenge = ((LivingEntity) entity).m_142581_();
                if (isAlly(revenge, to)) return true;
            }
            if (entity instanceof Mob && ((Mob) entity).m_5448_() != null) {
                LivingEntity attack = ((Mob) entity).m_5448_();
                return isAlly(attack, to);
            }
        }
        return false;
    }
}
