package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class DamageKnockbackEvent extends LivingEvent {
    protected final double originalStrength;
    protected final Vec3 originalDirection;
    protected DamageSource ds;
    protected double strength;
    protected Vec3 direction;

    public DamageKnockbackEvent(LivingEntity target, DamageSource source, double strength, Vec3 direction) {
        super(target);
        this.strength = this.originalStrength = strength;
        originalDirection=this.direction=direction;
        ds = source;
    }

    public double getStrength() {return this.strength;}

    public void setStrength(double strength) {this.strength = strength;}
    public double getOriginalStrength() {return this.originalStrength;}

    public Vec3 getDirection() {
        return direction;
    }

    public DamageKnockbackEvent setDirection(Vec3 direction) {
        this.direction = direction;
        return this;
    }

    public Vec3 getOriginalDirection() {
        return originalDirection;
    }

    public DamageSource getDamageSource() {
        return ds;
    }
}
