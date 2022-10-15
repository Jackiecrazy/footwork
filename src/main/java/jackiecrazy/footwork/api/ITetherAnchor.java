package jackiecrazy.footwork.api;

import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface ITetherAnchor {
    /**
     * updates the tether wielder's velocity
     */
    default void updateTetheringVelocity() {
        if (getTetheringEntity() != null) {
            Vec3 offset = getTetheredOffset();
            Entity toBeMoved = getTetheringEntity();
            Entity moveTowards = getTetheredEntity();
            if (toBeMoved != null) {
                double distsq = 0;
                Vec3 point = null;
                if (offset != null) {
                    distsq = GeneralUtils.getDistSqCompensated(toBeMoved, offset);
                    point = offset;
                    if (moveTowards != null) {
                        distsq = toBeMoved.m_20280_(moveTowards);
                        point = moveTowards.m_20182_().m_82549_(offset);
                    }
                }
                //update the entity's relative position to the point
                //if the distance is below tether length, do nothing
                //if the distance is above tether length, apply centripetal force to the point
                if (getTetherLength() * getTetherLength() < distsq && point != null) {
                    toBeMoved.m_5997_((point.f_82479_ - toBeMoved.m_20185_()) * 0.05, (point.f_82480_ - toBeMoved.m_20186_()) * 0.05, (point.f_82481_ - toBeMoved.m_20189_()) * 0.05);
                }
                if (shouldRepel() && getTetherLength() * getTetherLength() < distsq && point != null) {
                    toBeMoved.m_5997_((point.f_82479_ - toBeMoved.m_20185_()) * -0.05, (point.f_82480_ - toBeMoved.m_20186_()) * -0.05, (point.f_82481_ - toBeMoved.m_20189_()) * -0.05);
                }
                if (getTetherLength() == 0 && moveTowards != null) {//special case to help with catching up to entities
                    //System.out.println(target.getDistanceSq(e));
                    //if(NeedyLittleThings.getDistSqCompensated(moveTowards, toBeMoved)>8){
                    toBeMoved.m_6034_(moveTowards.m_20185_(), moveTowards.m_20186_(), moveTowards.m_20189_());
                    //}
                    Vec3 vec = moveTowards.m_20184_();
                    toBeMoved.m_20256_(moveTowards.m_20184_());
                    if (!moveTowards.m_20096_())
                        toBeMoved.m_6001_(vec.f_82479_, moveTowards.m_20096_() ? 0 : vec.f_82480_, vec.f_82481_);
                }//else e.motionZ=e.motionX=e.motionY=0;
                toBeMoved.f_19864_ = true;
            }
        }
    }

    Entity getTetheringEntity();

    void setTetheringEntity(Entity to);

    @Nullable
    Vec3 getTetheredOffset();

    @Nullable
    Entity getTetheredEntity();

    void setTetheredEntity(Entity to);

    double getTetherLength();

    default boolean shouldRepel() {
        return false;
    }
}
