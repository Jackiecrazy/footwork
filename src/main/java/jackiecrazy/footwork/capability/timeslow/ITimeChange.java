package jackiecrazy.footwork.capability.timeslow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public interface ITimeChange {
    void alterSpeed(int ticks, double speed);

    /**
     * @return -1 to skip this tick, 0 for no change, positive values to tick again
     */
    int tickDown(int currentTick);
    void resetSpeed();
    void setRawSpeed(int ticks, double speed);
    float getPartialTick(float originalPT);
    double getEffectiveSpeed();
    int getTimeRemaining();
}
