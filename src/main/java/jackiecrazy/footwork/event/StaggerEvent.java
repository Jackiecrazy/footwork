package jackiecrazy.footwork.event;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class StaggerEvent extends LivingEvent {
    private final LivingEntity attacker;
    private int length, count;

    public StaggerEvent(LivingEntity entity, LivingEntity attacker, int staggerTime, int staggerCount) {
        super(entity);
        length = staggerTime;
        count = staggerCount;
        this.attacker = attacker;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }
}
