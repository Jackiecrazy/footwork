package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
@Cancelable
/**
 * default for... default, allow to bypass config hard cap
 */
public class ConsumePostureEvent extends LivingEvent {
    private final float original;
    private final LivingEntity attacker;
    private float amount;
    private boolean resetCooldown;

    public ConsumePostureEvent(LivingEntity entity, LivingEntity attacker, float amnt) {
        super(entity);
        amount = original = amnt;
        this.attacker = attacker;
        resetCooldown = true;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getOriginal() {
        return original;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }

    public boolean resetsCooldown() {
        return resetCooldown;
    }

    public void setResetCooldown(boolean reset) {
        resetCooldown = reset;
    }
}
