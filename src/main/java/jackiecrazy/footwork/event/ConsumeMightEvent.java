package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * cancel to not consume might
 * allow to permit subsequent execution, deny to prevent. Default is if there's enough might.
 * if not canceled and denied, the might will be consumed but will never execute.
 * if not canceled and allowed, the might will be consumed to the limit, but will always execute.
 */
public class ConsumeMightEvent extends LivingEvent implements ICancellableEvent {
    private final float original;
    private float amount;
    private final float above;
    public ConsumeMightEvent(LivingEntity entity, float amnt, float above) {
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

    private TriState execute=TriState.DEFAULT;
    public TriState shouldExecute() {
        return execute;
    }

    public void setCanExecute(TriState execute) {
        this.execute = execute;
    }
}
