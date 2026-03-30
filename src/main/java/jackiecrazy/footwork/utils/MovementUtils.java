package jackiecrazy.footwork.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Iterator;
import java.util.List;

public class MovementUtils {
    /**
     * Checks the +x, -x, +y, -y, +z, -z, in that order
     */
    public static boolean willHitWall(Entity elb) {
        double allowance = 1;
        AABB aabb = elb.getBoundingBox();
        Vec3 motion = elb.getDeltaMovement();
        return elb.level().collidesWithSuffocatingBlock(elb, aabb.expandTowards(motion.x, motion.y, motion.z));
    }

    public static boolean willCollide(Entity elb) {
        double allowance = 1;
        return willHitWall(elb) || collidingEntity(elb) != null;
        //return elb.level().collidesWithSuffocatingBlock(elb.getBoundingBox().inflate(elb.motionX * allowance, elb.motionY * allowance, elb.motionZ * allowance));// || collidingEntity(elb) != null;
    }

    public static boolean willHitWallFrom(Entity elb, Entity from) {
        double allowance = 1;
        AABB aabb = elb.getBoundingBox();
        Vec3 motion = elb.position().subtract(from.position()).normalize();
        return elb.level().collidesWithSuffocatingBlock(elb, aabb.expandTowards(motion.x, motion.y, motion.z));
    }

    public static Entity collidingEntity(Entity elb) {
        AABB aabb = elb.getBoundingBox();
        Vec3 motion = elb.getDeltaMovement();
        List<Entity> entities = elb.level().getEntities(elb, aabb.expandTowards(motion.x, motion.y * 6, motion.z));
        double dist = 0;
        Entity pick = null;
        for (Entity e : entities) {
            if (e.distanceToSqr(elb) < dist || dist == 0) {
                pick = e;
                dist = e.distanceToSqr(elb);
            }
        }
        return pick;
    }

    public static boolean isTouchingWall(Entity elb) {
        AABB aabb = elb.getBoundingBox();
        Iterator<VoxelShape> boxes = elb.level().getCollisions(elb, aabb.inflate(0.1)).iterator();
        return boxes.hasNext();
    }

    public static void applyVelocity(Vec3 vec, LivingEntity e, boolean set) {
        final Vec3 vel = resolveVelocity(e.getLookAngle(), vec);
        final boolean iszero = vel.lengthSqr()==0;
        if (set && !iszero) {
            e.setDeltaMovement(vel);
        } else e.addDeltaMovement(vel);
        if (set || !iszero)
            e.hurtMarked = true;
    }

    public static Vec3 resolveVelocity(Vec3 forward, Vec3 direction) {
        // Create right and up basis vectors
        double Y = direction.y;
        direction = direction.multiply(1, 0, 1);
        Vec3 globalUp = new Vec3(0, 1, 0);
        Vec3 right = forward.cross(globalUp).normalize();
        Vec3 up = right.cross(forward).normalize();  // Ensure orthogonal

        Vec3 lookAdjusted = forward.normalize().scale(direction.z)
                .add(right.scale(direction.x))
                .add(up.scale(direction.y));
        if (Y != 0) lookAdjusted = new Vec3(lookAdjusted.x, Y, lookAdjusted.z);

        return lookAdjusted;
    }
}
