package jackiecrazy.footwork.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class MeleeDamageSourceEvent extends LivingEvent {
    private final DamageSource original;
    private DamageSource damageSource;
    private Entity target;

    public Entity getTarget() {
        return target;
    }

    public MeleeDamageSourceEvent(LivingEntity entity, Entity target, DamageSource ds) {
        super(entity);
        damageSource=original=ds;
        this.target=target;
    }

    public DamageSource getOriginal() {
        return original;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public void setDamageSource(DamageSource damageSource) {
        this.damageSource = damageSource;
    }
}
