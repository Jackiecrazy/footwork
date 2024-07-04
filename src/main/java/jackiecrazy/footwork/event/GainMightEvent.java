package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class GainMightEvent extends LivingEvent {
    private float quantity;
    public GainMightEvent(LivingEntity subject, float amount) {
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
