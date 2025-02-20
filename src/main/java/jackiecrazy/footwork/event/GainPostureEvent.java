package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class GainPostureEvent extends LivingEvent {
    private float quantity;
    public GainPostureEvent(LivingEntity subject, float amount) {
        super(subject);
        quantity=amount;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
