package jackiecrazy.footwork.move.motionframe;

import net.minecraft.network.syncher.EntityDataSerializer;

public interface MotionManager {
    MotionFrame getNextPoint(int elapsedTicks);
    int getDuration();
    default boolean hasEnded(int atTick){
        return atTick>getDuration();
    }
    default MotionFrame getStartFrame(){
        return getNextPoint(0);
    }
    default MotionFrame getEndFrame(){
        return getNextPoint(getDuration());
    }
}
