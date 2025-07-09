package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class AttackAdrenalineEvent extends LivingEvent {
    private final LivingEntity attacker;
    private float quantity;
    public AttackAdrenalineEvent(LivingEntity attacker, LivingEntity defender, float amount) {
        super(defender);
        this.attacker=attacker;
        quantity=amount;
    }

    public LivingEntity getAttacker(){
        return attacker;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
