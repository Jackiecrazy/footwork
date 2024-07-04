package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class DamageKnockbackEvent extends LivingEvent {
    protected final double originalStrength;
    protected final double originalRatioX, originalRatioZ;
    protected DamageSource ds;
    protected double strength;
    protected double ratioX, ratioZ;

    public DamageKnockbackEvent(LivingEntity target, DamageSource source, double strength, double ratioX, double ratioZ) {
        super(target);
        this.strength = this.originalStrength = strength;
        this.ratioX = this.originalRatioX = ratioX;
        this.ratioZ = this.originalRatioZ = ratioZ;
        ds = source;
    }

    public double getStrength() {return this.strength;}

    public void setStrength(double strength) {this.strength = strength;}

    public double getRatioX() {return this.ratioX;}

    public void setRatioX(double ratioX) {this.ratioX = ratioX;}

    public double getRatioZ() {return this.ratioZ;}

    public void setRatioZ(double ratioZ) {this.ratioZ = ratioZ;}

    public double getOriginalStrength() {return this.originalStrength;}

    public double getOriginalRatioX() {return this.originalRatioX;}

    public double getOriginalRatioZ() {return this.originalRatioZ;}

    public DamageSource getDamageSource() {
        return ds;
    }
}
