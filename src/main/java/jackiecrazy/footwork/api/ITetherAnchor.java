package jackiecrazy.footwork.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface ITetherAnchor {
    /**
     * updates the tether wielder's velocity
     */
    default void updateTetheringVelocity() {
        Vec3 offset = getTetheredOffset();
        Entity toBeMoved = getTetheringEntity();
        Entity moveTowards = getTetheredEntity();
        if (toBeMoved != null) {
            moveTargetTowards(toBeMoved, moveTowards == null ? offset : moveTowards.position().add(offset), 0.05);
            if (getTetherLength() < 0 && moveTowards != null) {//special case to help with catching up to entities
                toBeMoved.setPos(moveTowards.getX(), moveTowards.getY(), moveTowards.getZ());
                Vec3 vec = moveTowards.getDeltaMovement();
                toBeMoved.setDeltaMovement(moveTowards.getDeltaMovement());
                if (!moveTowards.onGround())
                    toBeMoved.lerpMotion(vec.x, moveTowards.onGround() ? 0 : vec.y, vec.z);
            }
        }
    }

    default void moveTargetTowards(Entity toBeMoved, Vec3 point, double force) {
        double length = Math.max(getTetherLength(), 0);
        double distsq = toBeMoved.distanceToSqr(point);
//        if (offset != null) {
//            distsq = GeneralUtils.getDistSqCompensated(toBeMoved, offset);
//            point = offset;
//            if (moveTowards != null) {
//                distsq = toBeMoved.distanceToSqr(moveTowards);
//                point = moveTowards.position().add(offset);
//            }
//        }
        Vec3 finalMomentum = Vec3.ZERO;
        if (!stopMotion()) finalMomentum = toBeMoved.getDeltaMovement();
        //update the entity's relative position to the point
        //if the distance is below tether length, do nothing
        //if the distance is above tether length, apply centripetal force to the point
        if (length * length < distsq) {
            Vec3 modify = new Vec3((point.x - toBeMoved.getX()) * force, (point.y - toBeMoved.getY()) * force, (point.z - toBeMoved.getZ()) * force);
            toBeMoved.setDeltaMovement(finalMomentum.add(modify));
            toBeMoved.hasImpulse = true;
        }
        if (shouldRepel() && length * length > distsq) {
            Vec3 modify = new Vec3((point.x - toBeMoved.getX()) * -force, (point.y - toBeMoved.getY()) * -force, (point.z - toBeMoved.getZ()) * -force);
            toBeMoved.setDeltaMovement(finalMomentum.add(modify));
            toBeMoved.hasImpulse = true;
        }//else e.motionZ=e.motionX=e.motionY=0;
        toBeMoved.hurtMarked = true;
    }

    default boolean stopMotion() {
        return false;
    }

    Entity getTetheringEntity();

    void setTetheringEntity(Entity to);

    @NotNull
    Vec3 getTetheredOffset();

    @Nullable
    Entity getTetheredEntity();

    void setTetheredEntity(Entity to);

    double getTetherLength();

    default boolean shouldRepel() {
        return false;
    }
}
