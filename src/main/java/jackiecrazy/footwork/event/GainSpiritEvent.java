package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class GainSpiritEvent extends LivingEvent {
    private int quantity;
    public GainSpiritEvent(LivingEntity subject, int amount) {
        super(subject);
        quantity=amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
