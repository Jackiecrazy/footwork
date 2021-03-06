package jackiecrazy.footwork.api;

import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface ITetherItem {
    /**
     * updates the tether wielder's velocity
     */
    default void updateTetheringVelocity(ItemStack stack, LivingEntity wielder) {
        if (getTetheringEntity(stack, wielder) != null) {
            Vector3d offset = getTetheredOffset(stack, wielder);
            Entity toBeMoved = getTetheringEntity(stack, wielder);
            Entity moveTowards = getTetheredEntity(stack, wielder);
            if (toBeMoved != null) {
                double distsq = 0;
                Vector3d point = null;
                if (offset != null) {
                    distsq = GeneralUtils.getDistSqCompensated(toBeMoved, offset);
                    point = offset;
                    if (moveTowards != null) {
                        distsq = toBeMoved.distanceToSqr(moveTowards);
                        point = moveTowards.position().add(offset);
                    }
                }else if (moveTowards != null) {
                    distsq = toBeMoved.distanceToSqr(moveTowards);
                    point = moveTowards.position();
                }
                //update the entity's relative position to the point
                //if the distance is below tether length, do nothing
                //if the distance is above tether length, apply centripetal force to the point
                if (getTetherLength(stack) * getTetherLength(stack) < distsq && point != null) {
                    toBeMoved.push((point.x - toBeMoved.getX()) * 0.01, (point.y - toBeMoved.getY()) * 0.01, (point.z - toBeMoved.getZ()) * 0.01);
                }
                if (shouldRepel(stack) && getTetherLength(stack) * getTetherLength(stack) > distsq*2 && point != null) {
                    toBeMoved.push((point.x - toBeMoved.getX()) * -0.01, (point.y - toBeMoved.getY()) * -0.01, (point.z - toBeMoved.getZ()) * -0.01);
                }
                if (getTetherLength(stack) == 0 && moveTowards != null) {//special case to help with catching up to entities
                    //System.out.println(target.getDistanceSq(e));
                    //if(NeedyLittleThings.getDistSqCompensated(moveTowards, toBeMoved)>8){
                    toBeMoved.setPos(moveTowards.getX(), moveTowards.getY(), moveTowards.getZ());
                    //}
                    Vector3d vec = moveTowards.getDeltaMovement();
                    toBeMoved.setDeltaMovement(moveTowards.getDeltaMovement());
                    if (!moveTowards.isOnGround())
                        toBeMoved.lerpMotion(vec.x, moveTowards.isOnGround() ? 0 : vec.y, vec.z);
                }//else e.motionZ=e.motionX=e.motionY=0;
                toBeMoved.hurtMarked = true;
            }
        }
    }

    Entity getTetheringEntity(ItemStack stack, LivingEntity wielder);

    @Nullable
    Vector3d getTetheredOffset(ItemStack stack, LivingEntity wielder);

    @Nullable
    Entity getTetheredEntity(ItemStack stack, LivingEntity wielder);

    double getTetherLength(ItemStack stack);

    default boolean shouldRepel(ItemStack stack) {
        return false;
    }

    default boolean renderTether(ItemStack stack) {
        return false;
    }
}
