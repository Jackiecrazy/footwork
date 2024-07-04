package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;

public class ExposeEvent extends StunEvent  {

    public ExposeEvent(LivingEntity entity, LivingEntity attacker, int staggerTime) {
        super(entity, attacker, staggerTime, true);
    }
}
