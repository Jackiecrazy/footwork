package jackiecrazy.footwork.api;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
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

    default void swing() {
        // --- variables ---
        final Entity toMove = getTetheringEntity();
        final Entity moveTo = getTetheredEntity();
        if (toMove == null || moveTo == null) return;
        Vec3 hookPos = moveTo.position().add(getTetheredOffset());
        Vec3 playerEyePos = toMove.getEyePosition();
        Vec3 vecToHook = hookPos.subtract(playerEyePos);
        Vec3 unitVector = vecToHook.normalize();
        double maxDistance = getTetherLength();
        double dist = vecToHook.length();

        Vec3 velocity = toMove.getDeltaMovement().multiply(1.05, 0.9, 1.05);
        double vRadial = velocity.dot(unitVector);
        Vec3 vTangential = velocity.subtract(unitVector.scale(vRadial));

        double vTangentialMultiplier = 1.01;


        if (dist > maxDistance) {
            double stretch = dist - maxDistance;

            vTangentialMultiplier = 1.047;

            double new_vRadial = stretch * 0.07;
            if (vRadial <= new_vRadial) vRadial = new_vRadial;
        }


        if (!toMove.onGround() && (!(toMove instanceof Player p) || p.isFallFlying())) {
            vTangential = vTangential.scale(vTangentialMultiplier);
            vRadial = vRadial * 0.99;
        }

        Vec3 finalVelocity = vTangential.add(unitVector.scale(vRadial));//.multiply(0.5, 1.11, 0.5);

        toMove.setDeltaMovement(finalVelocity);


        if (!toMove.level().isClientSide()) {
            // --- server logic for fall damage reset ---
            toMove.resetFallDistance();
            if (!toMove.onGround()) {
                toMove.hurtMarked = false;
                if ((dist + 0.6) > maxDistance) {
                    if (unitVector.y > -0.15) {
                        toMove.resetFallDistance();
                    }
                }
            }
        }
    }

    default void moveTargetTowards(Entity toBeMoved, Vec3 point, double force) {
        double length = Math.max(getTetherLength(), 0);
        Vec3 currentPos = toBeMoved.position();
        if (fuzzyTargeting()) {
            AABB entityBB = toBeMoved.getBoundingBox();
            currentPos = new Vec3(
                    Mth.clamp(point.x, entityBB.minX, entityBB.maxX),
                    Mth.clamp(point.y, entityBB.minY, entityBB.maxY),
                    Mth.clamp(point.z, entityBB.minZ, entityBB.maxZ)
            );
        }
        double distsq = currentPos.distanceToSqr(point);
        if (cappedForce())
            force = Math.min(Math.sqrt(distsq), force);
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
            Vec3 modify = new Vec3((point.x - currentPos.x()), (point.y - currentPos.y()), (point.z - currentPos.z())).normalize().scale(force);
            final Vec3 addVel = finalMomentum.add(modify);
//            final Vec3 apply = toBeMoved.position().add(finalMomentum).distanceToSqr(point) > toBeMoved.position().add(addVel).distanceToSqr(point)
            toBeMoved.setDeltaMovement(addVel);
            toBeMoved.hasImpulse = true;
        }
        if (shouldRepel() && length * length > distsq) {
            Vec3 modify = new Vec3((point.x - currentPos.x()), (point.y - currentPos.y()), (point.z - currentPos.z())).normalize().scale(-force);
            final Vec3 addVel = finalMomentum.add(modify);
            toBeMoved.setDeltaMovement(addVel);
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

    default boolean fuzzyTargeting() {
        return false;
    }

    default boolean cappedForce() {
        return false;
    }
}
