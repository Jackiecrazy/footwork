package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class DodgeEvent extends LivingEvent implements ICancellableEvent {
    private double force;
    private Direction side;

    public DodgeEvent(LivingEntity subject, Direction d, double amount) {
        super(subject);
        force = amount;
        side = d;
    }

    public double getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public Direction getDirection() {return side;}

    public void setDirection(Direction d) {side = d;}

    public enum Direction {
        FORWARD,//slide
        BACK,
        LEFT,
        RIGHT
    }
}
