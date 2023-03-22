package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ExposeEvent extends LivingEvent {
    private final LivingEntity attacker;
    private int length;

    public ExposeEvent(LivingEntity entity, LivingEntity attacker, int staggerTime) {
        super(entity);
        length = staggerTime;
        this.attacker = attacker;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }
}
