package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class RegenPostureEvent extends LivingEvent {
    private float quantity;
    public RegenPostureEvent(LivingEntity subject, float amount) {
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
