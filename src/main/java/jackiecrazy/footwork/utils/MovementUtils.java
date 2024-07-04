package jackiecrazy.footwork.utils;

import net.minecraft.world.entity.Entity;
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
        Vec3 motion=elb.getDeltaMovement();
        return elb.level().collidesWithSuffocatingBlock(elb, aabb.expandTowards(motion.x,motion.y,motion.z));
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
        return elb.level().collidesWithSuffocatingBlock(elb, aabb.expandTowards(motion.x,motion.y,motion.z));
    }


//    public static boolean attemptJump(LivingEntity elb) {
//        //if you're on the ground, I'll let vanilla handle you
//        if (elb.isOnGround()||elb.isRiding()) return false;
//        ITaoStatCapability itsc = CombatData.getCap(elb);
//        if (!itsc.isInCombatMode()) return false;
//        //qi has to be nonzero
//        if (itsc.getQi() == 0) return false;
//        //mario mario, wherefore art thou mario? Ignores all other jump condition checks
//        Entity ent = collidingEntity(elb);
//        if (ent instanceof LivingEntity) {
//            kick(elb, (LivingEntity) ent);
//        } else {
//            //if you're exhausted or just jumped, you can't jump again
//            if ((itsc.getJumpState() == ITaoStatCapability.JUMPSTATE.EXHAUSTED || itsc.getJumpState() == ITaoStatCapability.JUMPSTATE.JUMPING))
//                return false;
//            if (itsc.getQi() > 3)
//                itsc.setJumpState(ITaoStatCapability.JUMPSTATE.JUMPING);
//            else itsc.setJumpState(ITaoStatCapability.JUMPSTATE.EXHAUSTED);
//        }
//        itsc.setClingDirections(new ITaoStatCapability.ClingData(false, false, false, false));
//        if (elb instanceof EntityPlayer)
//            ((EntityPlayer) elb).jump();
//        else try {
//            jump.invoke(elb);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        double speed = Math.sqrt(NeedyLittleThings.getSpeedSq(elb));
//        if (isTouchingWall(elb)) {
//            boolean[] dir = collisionStatus(elb);
//            Direction face = elb.getHorizontalFacing();
//            boolean facingWall = false;
//            switch (face) {
//                case WEST:
//                    facingWall = dir[0];
//                    break;
//                case EAST:
//                    facingWall = dir[1];
//                    break;
//                case NORTH:
//                    facingWall = dir[4];
//                    break;
//                case SOUTH:
//                    facingWall = dir[5];
//                    break;
//            }
//            //Vec3d look=elb.getLookVec();
//            if (dir[0] && !facingWall) {//east
//                elb.motionX += speed / 2;
//            }
//            if (dir[1] && !facingWall) {//west
//                elb.motionX -= speed / 2;
//            }
//            if (dir[4] && !facingWall) {//south
//                elb.motionZ += speed / 2;
//            }
//            if (dir[5] && !facingWall) {//north
//                elb.motionZ -= speed / 2;
//            }
//            if (!facingWall) elb.motionY /= 2;
//        }
//        elb.velocityChanged = true;
//        TaoCasterData.forceUpdateTrackingClients(elb);
//        return true;
//    }

//    public static boolean isInBulletTime(LivingEntity elb){
//        ITaoStatCapability cap=CombatData.getCap(elb);
//        return cap.getRollCounter()<20+2*cap.getQi();
//    }

        /**
     * Checks the +x, -x, +y, -y, +z, -z, in that order
     *
     * @param elb
     * @return
     */
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

//    public static void kick(LivingEntity elb, LivingEntity uke) {
//        if (elb.isRiding()) return;
//        uke.attackEntityFrom(DamageSource.FALLING_BLOCK, 1);
//        CombatData.getCap(uke).consumePosture(5, true, elb);
//        for (int i = 0; i < 10; ++i) {
//            double d0 = Taoism.unirand.nextGaussian() * 0.02D;
//            double d1 = Taoism.unirand.nextGaussian() * 0.02D;
//            double d2 = Taoism.unirand.nextGaussian() * 0.02D;
//            elb.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, uke.posX + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, uke.posY + 1.0D + (double) (Taoism.unirand.nextFloat() * uke.height), uke.posZ + (double) (Taoism.unirand.nextFloat() * uke.width * 2.0F) - (double) uke.width, d0, d1, d2);
//        }
//        elb.world.playSound(null, uke.posX, uke.posY, uke.posZ, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.PLAYERS, 0.5f + Taoism.unirand.nextFloat() * 0.5f, 0.85f + Taoism.unirand.nextFloat() * 0.3f);
//    }

    public static boolean isTouchingWall(Entity elb) {
        AABB aabb = elb.getBoundingBox();
        Iterator<VoxelShape> boxes = elb.level().getCollisions(elb, aabb.inflate(0.1)).iterator();
        return boxes.hasNext();
    }

//    public static boolean[] collisionStatus(Entity elb) {
//        double allowance = 0.1;
//        boolean[] ret = {false, false, false, false, false, false};
//        AABB aabb = elb.getBoundingBox();
//        Iterator<VoxelShape> boxes = elb.level().getCollisions(elb, aabb.inflate(allowance / 2)).iterator();
//        while (boxes.hasNext()) {
//            VoxelShape a = boxes.next();
//            if (aabb.calculateXOffset(a, allowance) != allowance) ret[0] = true;
//            if (aabb.calculateXOffset(a, -allowance) != -allowance) ret[1] = true;
//            if (aabb.calculateYOffset(a, allowance) != allowance) ret[2] = true;
//            if (aabb.calculateYOffset(a, -allowance) != -allowance) ret[3] = true;
//            if (aabb.calculateZOffset(a, allowance) != allowance) ret[4] = true;
//            if (aabb.calculateZOffset(a, -allowance) != -allowance) ret[5] = true;
//        }
//        return ret;
//    }

}
