package jackiecrazy.footwork.event;

import net.minecraft.entity.LivingEntity;
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
    private final float original;
    private float amount;
    private final float above;
    public ConsumeSpiritEvent(LivingEntity entity, float amnt, float above) {
        super(entity);
        amount=original=amnt;
        this.above=above;
    }

    public float getAbove(){
        return above;
    }

    public float getAmount(){
        return amount;
    }

    public float getOriginal() {
        return original;
    }

    public void setAmount(float amount){
        this.amount=amount;
    }
}
