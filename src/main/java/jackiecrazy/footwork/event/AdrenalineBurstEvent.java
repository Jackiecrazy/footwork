package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
@Cancelable
/**
 * cancel to not consume spirit
 * allow to return true, deny to return false
 * if not canceled and denied, the might will be consumed but will return false.
 * if not canceled and allowed, the might will be consumed to the limit, but will always return true.
 */
public class AdrenalineBurstEvent extends LivingEvent {
    public AdrenalineBurstEvent(LivingEntity entity) {
        super(entity);
    }
}
