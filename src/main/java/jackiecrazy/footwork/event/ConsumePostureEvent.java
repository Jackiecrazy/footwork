package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * default for... default, allow to bypass config hard cap
 */
public class ConsumePostureEvent extends LivingEvent implements ICancellableEvent {
    private final float original;
    private final LivingEntity attacker;
    private final float above;
    private float amount;
    private boolean resetCooldown;
    private TriState execute = TriState.DEFAULT;

    public ConsumePostureEvent(LivingEntity entity, LivingEntity attacker, float amnt, float above) {
        super(entity);
        amount = original = amnt;
        this.above = above;
        this.attacker = attacker;
        resetCooldown = true;
    }

    public float getAbove() {
        return above;
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

    public TriState shouldExecute() {
        return execute;
    }

    public void setCanExecute(TriState execute) {
        this.execute = execute;
    }
}
