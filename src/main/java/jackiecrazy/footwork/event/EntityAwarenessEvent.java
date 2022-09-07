package jackiecrazy.footwork.event;

import jackiecrazy.footwork.utils.StealthUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

public class EntityAwarenessEvent extends LivingEvent {
    private final StealthUtils.Awareness originalStatus;
    private LivingEntity attacker;
    private StealthUtils.Awareness status;

    public EntityAwarenessEvent(LivingEntity entity, LivingEntity attacker, StealthUtils.Awareness originally) {
        super(entity);
        this.attacker = attacker;
        originalStatus = status = originally;
    }

    public StealthUtils.Awareness getAwareness() {
        return status;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return attacker;
    }

    public void setAwareness(StealthUtils.Awareness newAwareness) {
        status = newAwareness;
    }

    public StealthUtils.Awareness getOriginalAwareness() {
        return originalStatus;
    }
}
