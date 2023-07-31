package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ExposeEvent extends StunEvent {

    public ExposeEvent(LivingEntity entity, LivingEntity attacker, int staggerTime) {
        super(entity, attacker, staggerTime, true);
    }
}
