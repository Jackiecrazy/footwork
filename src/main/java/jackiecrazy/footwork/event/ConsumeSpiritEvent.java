package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
@Event.HasResult
/**
 * cancel to not consume spirit
 * allow to return true, deny to return false
 * if not canceled and denied, the spirit will be consumed but will return false.
 * if not canceled and allowed, the spirit will be consumed to the limit, but will always return true.
 */
public class ConsumeSpiritEvent extends LivingEvent {
    private final int original;
    private int amount;
    public ConsumeSpiritEvent(LivingEntity entity, int amnt) {
        super(entity);
        amount=original=amnt;
    }

    public int getAmount(){
        return amount;
    }

    public float getOriginal() {
        return original;
    }

    public void setAmount(int amount){
        this.amount=amount;
    }
}
