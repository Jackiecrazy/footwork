package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class StunEvent extends LivingEvent {
    private final LivingEntity attacker;
    private int length;
    private boolean knockdown;

    public StunEvent(LivingEntity entity, LivingEntity attacker, int staggerTime, boolean knockdown) {
        super(entity);
        length = staggerTime;
        this.attacker = attacker;
        this.knockdown=knockdown;
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

    public boolean isKnockdown() {
        return knockdown;
    }

    public void setKnockdown(boolean knockdown) {
        this.knockdown = knockdown;
    }
}
